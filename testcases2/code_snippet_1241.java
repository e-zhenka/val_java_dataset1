@Override
    public void onTrigger(final ProcessContext context, final ProcessSession session) {
        final FlowFile original = session.get();
        if (original == null) {
            return;
        }

        final int depth = context.getProperty(SPLIT_DEPTH).asInteger();
        final ComponentLog logger = getLogger();

        final List<FlowFile> splits = new ArrayList<>();
        final String fragmentIdentifier = UUID.randomUUID().toString();
        final AtomicInteger numberOfRecords = new AtomicInteger(0);
        final XmlSplitterSaxParser parser = new XmlSplitterSaxParser(xmlTree -> {
            FlowFile split = session.create(original);
            split = session.write(split, out -> out.write(xmlTree.getBytes("UTF-8")));
            split = session.putAttribute(split, FRAGMENT_ID.key(), fragmentIdentifier);
            split = session.putAttribute(split, FRAGMENT_INDEX.key(), Integer.toString(numberOfRecords.getAndIncrement()));
            split = session.putAttribute(split, SEGMENT_ORIGINAL_FILENAME.key(), split.getAttribute(CoreAttributes.FILENAME.key()));
            splits.add(split);
        }, depth);

        final AtomicBoolean failed = new AtomicBoolean(false);
        session.read(original, rawIn -> {
            try (final InputStream in = new java.io.BufferedInputStream(rawIn)) {
                try {
                    final XMLReader reader = XmlUtils.createSafeSaxReader(saxParserFactory, parser);
                    reader.parse(new InputSource(in));
                } catch (final ParserConfigurationException | SAXException e) {
                    logger.error("Unable to parse {} due to {}", new Object[]{original, e});
                    failed.set(true);
                }
            }
        });

        if (failed.get()) {
            session.transfer(original, REL_FAILURE);
            session.remove(splits);
        } else {
            splits.forEach((split) -> {
                split = session.putAttribute(split, FRAGMENT_COUNT.key(), Integer.toString(numberOfRecords.get()));
                session.transfer(split, REL_SPLIT);
            });

            final FlowFile originalToTransfer = copyAttributesToOriginal(session, original, fragmentIdentifier, numberOfRecords.get());
            session.transfer(originalToTransfer, REL_ORIGINAL);
            logger.info("Split {} into {} FlowFiles", new Object[]{originalToTransfer, splits.size()});
        }
    }
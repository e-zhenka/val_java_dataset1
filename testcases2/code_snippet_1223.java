public void parse(
            InputStream stream, ContentHandler handler,
            Metadata metadata, ParseContext context)
            throws IOException, SAXException, TikaException {

        // The MP4Parser library accepts either a File, or a byte array
        // As MP4 video files are typically large, always use a file to
        //  avoid OOMs that may occur with in-memory buffering
        TemporaryResources tmp = new TemporaryResources();
        TikaInputStream tstream = TikaInputStream.get(stream, tmp);

        try (IsoFile isoFile = new IsoFile(tstream.getFile())) {
            tmp.addResource(isoFile);

            // Grab the file type box
            FileTypeBox fileType = getOrNull(isoFile, FileTypeBox.class);
            if (fileType != null) {
                // Identify the type
                MediaType type = MediaType.application("mp4");
                for (Map.Entry<MediaType, List<String>> e : typesMap.entrySet()) {
                    if (e.getValue().contains(fileType.getMajorBrand())) {
                        type = e.getKey();
                        break;
                    }
                }
                metadata.set(Metadata.CONTENT_TYPE, type.toString());

                if (type.getType().equals("audio")) {
                    metadata.set(XMPDM.AUDIO_COMPRESSOR, fileType.getMajorBrand().trim());
                }
            } else {
                // Some older QuickTime files lack the FileType
                metadata.set(Metadata.CONTENT_TYPE, "video/quicktime");
            }


            // Get the main MOOV box
            MovieBox moov = getOrNull(isoFile, MovieBox.class);
            if (moov == null) {
                // Bail out
                return;
            }


            XHTMLContentHandler xhtml = new XHTMLContentHandler(handler, metadata);
            xhtml.startDocument();

            handleMovieHeaderBox(moov, metadata, xhtml);
            handleTrackBoxes(moov, metadata, xhtml);

            // Get metadata from the User Data Box
            UserDataBox userData = getOrNull(moov, UserDataBox.class);
            if (userData != null) {
                extractGPS(userData, metadata);
                MetaBox metaBox = getOrNull(userData, MetaBox.class);

                // Check for iTunes Metadata
                // See http://atomicparsley.sourceforge.net/mpeg-4files.html and
                //  http://code.google.com/p/mp4v2/wiki/iTunesMetadata for more on these
                handleApple(metaBox, metadata, xhtml);
                // TODO Check for other kinds too
            }

            // All done
            xhtml.endDocument();

        } finally {
            tmp.dispose();
        }

    }
@Override
        public void processPacket(PacketContext context) {
            // TODO filter packets sent to processors based on registrations
            for (ProcessorEntry entry : processors) {
                long start = System.nanoTime();
                entry.processor().process(context);
                entry.addNanos(System.nanoTime() - start);
            }
        }
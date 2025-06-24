@Override
        public void processPacket(PacketContext context) {
            // TODO filter packets sent to processors based on registrations
            for (ProcessorEntry entry : processors) {
                try {
                    long start = System.nanoTime();
                    entry.processor().process(context);
                    entry.addNanos(System.nanoTime() - start);
                } catch (Exception e) {
                    log.warn("Packet processor {} threw an exception", entry.processor(), e);
                }
            }
        }
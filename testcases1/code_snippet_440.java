void resumeReadsInternal(boolean wakeup) {
        synchronized (lock) {
            boolean alreadyResumed = anyAreSet(state, STATE_READS_RESUMED);
            state |= STATE_READS_RESUMED;
            if (!alreadyResumed || wakeup) {
                if (!anyAreSet(state, STATE_IN_LISTENER_LOOP)) {
                    state |= STATE_IN_LISTENER_LOOP;
                    getFramedChannel().runInIoThread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                boolean moreData;
                                do {
                                    ChannelListener<? super R> listener = getReadListener();
                                    if (listener == null || !isReadResumed()) {
                                        return;
                                    }
                                    ChannelListeners.invokeChannelListener((R) AbstractFramedStreamSourceChannel.this, listener);
                                    //if writes are shutdown or we become active then we stop looping
                                    //we stop when writes are shutdown because we can't flush until we are active
                                    //although we may be flushed as part of a batch
                                    moreData = (frameDataRemaining > 0 && data != null) || !pendingFrameData.isEmpty() || anyAreSet(state, STATE_WAITNG_MINUS_ONE);
                                }
                                while (allAreSet(state, STATE_READS_RESUMED) && allAreClear(state, STATE_CLOSED) && moreData);
                            } finally {
                                state &= ~STATE_IN_LISTENER_LOOP;
                            }
                        }
                    });
                }
            }
        }
    }
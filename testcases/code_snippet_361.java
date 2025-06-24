@Override
    public void awaitWritable() throws IOException {
        if(Thread.currentThread() == getIoThread()) {
            throw UndertowMessages.MESSAGES.awaitCalledFromIoThread();
        }
        synchronized (lock) {
            if (anyAreSet(state, STATE_CLOSED) || broken) {
                return;
            }
            if (readyForFlush) {
                try {
                    waiterCount++;
                    //we need to re-check after incrementing the waiters count
                    if(readyForFlush && !anyAreSet(state, STATE_CLOSED) && !broken) {
                        lock.wait(awaitWritableTimeout);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new InterruptedIOException();
                } finally {
                    waiterCount--;
                }
            }
        }
    }
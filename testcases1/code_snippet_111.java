public Thread newThread(final Runnable r) {
            return doPrivileged(new PrivilegedAction<Thread>() {
                public Thread run() {
                    final Thread taskThread = new Thread(threadGroup, r, name + " task-" + getNextSeq(), stackSize);
                    // Mark the thread as daemon if the Options.THREAD_DAEMON has been set
                    if (markThreadAsDaemon) {
                        taskThread.setDaemon(true);
                    }
                    return taskThread;
                }
            });
        }
@Test(timeout = 10000)
    public void spliceToFile() throws Throwable {
        EventLoopGroup group = new EpollEventLoopGroup(1);
        File file = PlatformDependent.createTempFile("netty-splice", null, null);
        file.deleteOnExit();

        SpliceHandler sh = new SpliceHandler(file);
        ServerBootstrap bs = new ServerBootstrap();
        bs.channel(EpollServerSocketChannel.class);
        bs.group(group).childHandler(sh);
        bs.childOption(EpollChannelOption.EPOLL_MODE, EpollMode.LEVEL_TRIGGERED);
        Channel sc = bs.bind(NetUtil.LOCALHOST, 0).syncUninterruptibly().channel();

        Bootstrap cb = new Bootstrap();
        cb.group(group);
        cb.channel(EpollSocketChannel.class);
        cb.handler(new ChannelInboundHandlerAdapter());
        Channel cc = cb.connect(sc.localAddress()).syncUninterruptibly().channel();

        for (int i = 0; i < data.length;) {
            int length = Math.min(random.nextInt(1024 * 64), data.length - i);
            ByteBuf buf = Unpooled.wrappedBuffer(data, i, length);
            cc.writeAndFlush(buf);
            i += length;
        }

        while (sh.future2 == null || !sh.future2.isDone() || !sh.future.isDone()) {
            if (sh.exception.get() != null) {
                break;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                // Ignore.
            }
        }

        sc.close().sync();
        cc.close().sync();

        if (sh.exception.get() != null && !(sh.exception.get() instanceof IOException)) {
            throw sh.exception.get();
        }

        byte[] written = new byte[data.length];
        FileInputStream in = new FileInputStream(file);

        try {
            Assert.assertEquals(written.length, in.read(written));
            Assert.assertArrayEquals(data, written);
        } finally {
            in.close();
            group.shutdownGracefully();
        }
    }
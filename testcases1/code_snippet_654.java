private void enumerateChmDirectoryListingList(ChmItsfHeader chmItsHeader,
            ChmItspHeader chmItspHeader) throws TikaException {
        try {
            int startPmgl = chmItspHeader.getIndex_head();
            int stopPmgl = chmItspHeader.getUnknown_0024();
            int dir_offset = (int) (chmItsHeader.getDirOffset() + chmItspHeader
                    .getHeader_len());
            setDataOffset(chmItsHeader.getDataOffset());

            /* loops over all pmgls */
            byte[] dir_chunk = null;
            for (int i = startPmgl; i>=0; ) {
                dir_chunk = new byte[(int) chmItspHeader.getBlock_len()];
                int start = i * (int) chmItspHeader.getBlock_len() + dir_offset;
                dir_chunk = ChmCommons
                        .copyOfRange(getData(), start,
                                start +(int) chmItspHeader.getBlock_len());

                PMGLheader = new ChmPmglHeader();
                PMGLheader.parse(dir_chunk, PMGLheader);
                enumerateOneSegment(dir_chunk);
                
                i=PMGLheader.getBlockNext();
                dir_chunk = null;
            }
        } catch (ChmParsingException e) {
            LOG.warn("Chm parse exception", e);
        } finally {
            setData(null);
        }
    }
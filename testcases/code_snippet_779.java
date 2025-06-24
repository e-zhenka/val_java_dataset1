protected COSDictionary parseXref(long startXRefOffset) throws IOException
    {
        source.seek(startXRefOffset);
        long startXrefOffset = Math.max(0, parseStartXref());
        // check the startxref offset
        long fixedOffset = checkXRefOffset(startXrefOffset);
        if (fixedOffset > -1)
        {
            startXrefOffset = fixedOffset;
        }
        document.setStartXref(startXrefOffset);
        long prev = startXrefOffset;
        // ---- parse whole chain of xref tables/object streams using PREV reference
        Set<Long> prevSet = new HashSet<Long>();
        COSDictionary trailer = null;
        while (prev > 0)
        {
            // seek to xref table
            source.seek(prev);
            // skip white spaces
            skipSpaces();
            // save current position instead of prev due to skipped spaces
            prevSet.add(source.getPosition());
            // -- parse xref
            if (source.peek() == X)
            {
                // xref table and trailer
                // use existing parser to parse xref table
                if (!parseXrefTable(prev) || !parseTrailer())
                {
                    throw new IOException("Expected trailer object at offset "
                            + source.getPosition());
                }
                trailer = xrefTrailerResolver.getCurrentTrailer();
                // check for a XRef stream, it may contain some object ids of compressed objects 
                if(trailer.containsKey(COSName.XREF_STM))
                {
                    int streamOffset = trailer.getInt(COSName.XREF_STM);
                    // check the xref stream reference
                    fixedOffset = checkXRefOffset(streamOffset);
                    if (fixedOffset > -1 && fixedOffset != streamOffset)
                    {
                        LOG.warn("/XRefStm offset " + streamOffset + " is incorrect, corrected to " + fixedOffset);
                        streamOffset = (int)fixedOffset;
                        trailer.setInt(COSName.XREF_STM, streamOffset);
                    }
                    if (streamOffset > 0)
                    {
                        source.seek(streamOffset);
                        skipSpaces();
                        try
                        {
                            parseXrefObjStream(prev, false);
                        }
                        catch (IOException ex)
                        {
                            if (isLenient)
                            {
                                LOG.error("Failed to parse /XRefStm at offset " + streamOffset, ex);
                            }
                            else
                            {
                                throw ex;
                            }
                        }
                    }
                    else
                    {
                        if(isLenient)
                        {
                            LOG.error("Skipped XRef stream due to a corrupt offset:"+streamOffset);
                        }
                        else
                        {
                            throw new IOException("Skipped XRef stream due to a corrupt offset:"+streamOffset);
                        }
                    }
                }
                prev = trailer.getLong(COSName.PREV);
            }
            else
            {
                // parse xref stream
                prev = parseXrefObjStream(prev, true);
                trailer = xrefTrailerResolver.getCurrentTrailer();
            }
            if (prev > 0)
            {
                // check the xref table reference
                fixedOffset = checkXRefOffset(prev);
                if (fixedOffset > -1 && fixedOffset != prev)
                {
                    prev = fixedOffset;
                    trailer.setLong(COSName.PREV, prev);
                }
            }
            if (prevSet.contains(prev))
            {
                throw new IOException("/Prev loop at offset " + prev);
            }
        }
        // ---- build valid xrefs out of the xref chain
        xrefTrailerResolver.setStartxref(startXrefOffset);
        trailer = xrefTrailerResolver.getTrailer();
        document.setTrailer(trailer);
        document.setIsXRefStream(XRefType.STREAM == xrefTrailerResolver.getXrefType());
        // check the offsets of all referenced objects
        checkXrefOffsets();
        // copy xref table
        document.addXRefTable(xrefTrailerResolver.getXrefTable());
        return trailer;
    }
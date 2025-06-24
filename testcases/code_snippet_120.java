public void parse(
            InputStream stream, ContentHandler handler,
            Metadata metadata, ParseContext context)
            throws IOException, SAXException, TikaException {

        // The MP4Parser library accepts either a File, or a byte array
        // As MP4 video files are typically large, always use a file to
        //  avoid OOMs that may occur with in-memory buffering
        TemporaryResources tmp = new TemporaryResources();
        TikaInputStream tstream = TikaInputStream.get(stream, tmp);

        try (DataSource dataSource = new DirectFileReadDataSource(tstream.getFile())) {
            try (IsoFile isoFile = new IsoFile(dataSource)) {
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


                // Pull out some information from the header box
                MovieHeaderBox mHeader = getOrNull(moov, MovieHeaderBox.class);
                if (mHeader != null) {
                    // Get the creation and modification dates
                    metadata.set(Metadata.CREATION_DATE, mHeader.getCreationTime());
                    metadata.set(TikaCoreProperties.MODIFIED, mHeader.getModificationTime());

                    // Get the duration
                    double durationSeconds = ((double) mHeader.getDuration()) / mHeader.getTimescale();
                    metadata.set(XMPDM.DURATION, DURATION_FORMAT.format(durationSeconds));

                    // The timescale is normally the sampling rate
                    metadata.set(XMPDM.AUDIO_SAMPLE_RATE, (int) mHeader.getTimescale());
                }


                // Get some more information from the track header
                // TODO Decide how to handle multiple tracks
                List<TrackBox> tb = moov.getBoxes(TrackBox.class);
                if (tb.size() > 0) {
                    TrackBox track = tb.get(0);

                    TrackHeaderBox header = track.getTrackHeaderBox();
                    // Get the creation and modification dates
                    metadata.set(TikaCoreProperties.CREATED, header.getCreationTime());
                    metadata.set(TikaCoreProperties.MODIFIED, header.getModificationTime());

                    // Get the video with and height
                    metadata.set(Metadata.IMAGE_WIDTH, (int) header.getWidth());
                    metadata.set(Metadata.IMAGE_LENGTH, (int) header.getHeight());

                    // Get the sample information
                    SampleTableBox samples = track.getSampleTableBox();
                    if (samples !=  null) {
                        SampleDescriptionBox sampleDesc = samples.getSampleDescriptionBox();
                        if (sampleDesc != null) {
                            // Look for the first Audio Sample, if present
                            AudioSampleEntry sample = getOrNull(sampleDesc, AudioSampleEntry.class);
                            if (sample != null) {
                                XMPDM.ChannelTypePropertyConverter.convertAndSet(metadata, sample.getChannelCount());
                                //metadata.set(XMPDM.AUDIO_SAMPLE_TYPE, sample.getSampleSize());    // TODO Num -> Type mapping
                                metadata.set(XMPDM.AUDIO_SAMPLE_RATE, (int) sample.getSampleRate());
                                //metadata.set(XMPDM.AUDIO_, sample.getSamplesPerPacket());
                                //metadata.set(XMPDM.AUDIO_, sample.getBytesPerSample());
                            }
                        }
                    }
                }

                // Get metadata from the User Data Box
                UserDataBox userData = getOrNull(moov, UserDataBox.class);
                if (userData != null) {
                    extractGPS(userData, metadata);
                    MetaBox meta = getOrNull(userData, MetaBox.class);

                    // Check for iTunes Metadata
                    // See http://atomicparsley.sourceforge.net/mpeg-4files.html and
                    //  http://code.google.com/p/mp4v2/wiki/iTunesMetadata for more on these
                    AppleItemListBox apple = getOrNull(meta, AppleItemListBox.class);
                    if (apple != null) {
                        // Title
                        AppleNameBox title = getOrNull(apple, AppleNameBox.class);
                        addMetadata(TikaCoreProperties.TITLE, metadata, title);

                        // Artist
                        AppleArtistBox artist = getOrNull(apple, AppleArtistBox.class);
                        addMetadata(TikaCoreProperties.CREATOR, metadata, artist);
                        addMetadata(XMPDM.ARTIST, metadata, artist);

                        // Album Artist
                        AppleArtist2Box artist2 = getOrNull(apple, AppleArtist2Box.class);
                        addMetadata(XMPDM.ALBUM_ARTIST, metadata, artist2);

                        // Album
                        AppleAlbumBox album = getOrNull(apple, AppleAlbumBox.class);
                        addMetadata(XMPDM.ALBUM, metadata, album);

                        // Composer
                        AppleTrackAuthorBox composer = getOrNull(apple, AppleTrackAuthorBox.class);
                        addMetadata(XMPDM.COMPOSER, metadata, composer);

                        // Genre
                        AppleGenreBox genre = getOrNull(apple, AppleGenreBox.class);
                        addMetadata(XMPDM.GENRE, metadata, genre);

                        // Year
                        AppleRecordingYear2Box year = getOrNull(apple, AppleRecordingYear2Box.class);
                        if (year != null) {
                            metadata.set(XMPDM.RELEASE_DATE, year.getValue());
                        }

                        // Track number
                        AppleTrackNumberBox trackNum = getOrNull(apple, AppleTrackNumberBox.class);
                        if (trackNum != null) {
                            metadata.set(XMPDM.TRACK_NUMBER, trackNum.getA());
                            //metadata.set(XMPDM.NUMBER_OF_TRACKS, trackNum.getB()); // TODO
                        }

                        // Disc number
                        AppleDiskNumberBox discNum = getOrNull(apple, AppleDiskNumberBox.class);
                        if (discNum != null) {
                            metadata.set(XMPDM.DISC_NUMBER, discNum.getA());
                        }

                        // Compilation
                        AppleCompilationBox compilation = getOrNull(apple, AppleCompilationBox.class);
                        if (compilation != null) {
                            metadata.set(XMPDM.COMPILATION, (int) compilation.getValue());
                        }

                        // Comment
                        AppleCommentBox comment = getOrNull(apple, AppleCommentBox.class);
                        addMetadata(XMPDM.LOG_COMMENT, metadata, comment);

                        // Encoder
                        AppleEncoderBox encoder = getOrNull(apple, AppleEncoderBox.class);
                        if (encoder != null) {
                            metadata.set(XMP.CREATOR_TOOL, encoder.getValue());
                        }


                        // As text
                        for (Box box : apple.getBoxes()) {
                            if (box instanceof Utf8AppleDataBox) {
                                xhtml.element("p", ((Utf8AppleDataBox) box).getValue());
                            }
                        }
                    }

                    // TODO Check for other kinds too
                }


                // All done
                xhtml.endDocument();
            }
        } finally {
            tmp.dispose();
        }

    }
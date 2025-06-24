private static void processZipStream(File dir, InputStream inputStream) throws IOException
   {
      String canonicalDestinationDirPath = dir.getCanonicalPath();
      ZipInputStream zip = new ZipInputStream(inputStream);
      while (true)
      {
         ZipEntry entry = zip.getNextEntry();
         if (entry == null)
         {
            break;
         }

         File file = new File(dir, entry.getName());

         // https://snyk.io/research/zip-slip-vulnerability
         String canonicalDestinationFile = file.getCanonicalPath();
         if (!canonicalDestinationFile.startsWith(canonicalDestinationDirPath + File.separator))
         {
            throw new IOException("Entry is outside of the target dir: " + entry.getName());
         }

         if (entry.isDirectory())
         {
            FileHelper.mkdirsQuietly(file);
            continue;
         }

         File parent = file.getParentFile();
         if (parent != null)
         {
            FileHelper.mkdirsQuietly(parent);
         }

         FileOutputStream fos = new FileOutputStream(file);
         byte[] bytes = new byte[1024];
         int length;
         while ((length = zip.read(bytes)) >= 0)
         {
            fos.write(bytes, 0, length);
         }
         fos.close();
      }
   }
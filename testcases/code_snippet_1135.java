public Column<T, V> setCaption(String caption) {
            Objects.requireNonNull(caption, "Header caption can't be null");
            caption = Jsoup.parse(caption).text();
            if (caption.equals(getState(false).caption)) {
                return this;
            }
            getState().caption = caption;

            HeaderRow row = getGrid().getDefaultHeaderRow();
            if (row != null) {
                row.getCell(this).setText(caption);
            }

            return this;
        }
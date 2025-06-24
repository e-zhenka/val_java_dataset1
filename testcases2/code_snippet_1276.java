protected int indexOf(char c, int pos) {
		for (int i = pos; pos < len; i++)
			if (in[i] == (byte) c)
				return i;
		return -1;
	}
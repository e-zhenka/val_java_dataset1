protected Number extractFloat() throws ParseException {
		if (!acceptLeadinZero)
			checkLeadinZero();
		try {
			if (!useHiPrecisionFloat)
				return Float.parseFloat(xs);
			if (xs.length() > 18) // follow JSonIJ parsing method
				return new BigDecimal(xs);
			return Double.parseDouble(xs);
		} catch (NumberFormatException e) {
			throw new ParseException(pos, ERROR_UNEXPECTED_TOKEN, xs);
		}
	}
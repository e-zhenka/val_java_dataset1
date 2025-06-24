protected Number extractFloat() throws ParseException {
		if (!acceptLeadinZero)
			checkLeadinZero();
		if (!useHiPrecisionFloat)
			return Float.parseFloat(xs);
		if (xs.length() > 18) // follow JSonIJ parsing method
			return new BigDecimal(xs);
		return Double.parseDouble(xs);
	}
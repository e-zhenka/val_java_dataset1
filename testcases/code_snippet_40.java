private void normalizeNumber(int start, int end) {
    int pos = start;
    // Sign
    if (pos < end) {
      switch (jsonish.charAt(pos)) {
        case '+':
          elide(pos, pos + 1);
          ++pos;
          break;
        case '-':
          ++pos;
          break;
        default:
          break;
      }
    }

    // Integer part
    int intEnd = endOfDigitRun(pos, end);
    if (pos == intEnd) {  // No empty integer parts allowed in JSON.
      insert(pos, '0');
    } else if ('0' == jsonish.charAt(pos)) {
      boolean reencoded = false;
      int maxDigVal = 0; // The value of the max digit
      int probableBase = 10; // The base suggested by the prefix
      int firstDigitIndex = -1;
      if (intEnd - pos == 1 && intEnd < end
              && 'x' == (jsonish.charAt(intEnd) | 32)) {  // Recode hex.
        probableBase = 16;
        firstDigitIndex = intEnd + 1;
        for (intEnd = intEnd + 1; intEnd < end; ++intEnd) {
          char ch = jsonish.charAt(intEnd);
          int digVal;
          if ('0' <= ch && ch <= '9') {
            digVal = ch - '0';
          } else {
            ch |= 32;
            if ('a' <= ch && ch <= 'f') {
              digVal = ch - ('a' - 10);
            } else {
              break;
            }
          }
          maxDigVal = Math.max(digVal, maxDigVal);
        }
        reencoded = true;
      } else if (intEnd - pos > 1) {  // Recode octal.
        probableBase = 8;
        firstDigitIndex = pos;
        for (int i = pos; i < intEnd; ++i) {
          int digVal = jsonish.charAt(i) - '0';
          if (digVal < 0) {
            break;
          }
          maxDigVal = Math.max(digVal, maxDigVal);
        }
        reencoded = true;
      }
      if (reencoded) {
        // Avoid multiple signs.
        // Putting out the underflowed value is the least bad option.
        elide(pos, intEnd);

        String digits = jsonish.substring(firstDigitIndex, intEnd);

        int nDigits = digits.length();
        int base = probableBase > maxDigVal ? probableBase : maxDigVal > 10 ? 16 : 10;
        if (nDigits == 0) {
          sanitizedJson.append('0');
        } else if (DIGITS_BY_BASE_THAT_FIT_IN_63B[base] >= nDigits) {
          long value = Long.parseLong(digits, base);
          sanitizedJson.append(value);
        } else {
          // If there are lots of digits, we need to reencode using a BigInteger
          BigInteger value = new BigInteger(digits, base);
          sanitizedJson.append(value);
        }
      }
    }
    pos = intEnd;

    // Optional fraction.
    if (pos < end && jsonish.charAt(pos) == '.') {
      ++pos;
      int fractionEnd = endOfDigitRun(pos, end);
      if (fractionEnd == pos) {
        insert(pos, '0');
      }
      // JS eval will discard digits after 24(?) but will not treat them as a
      // syntax error, and JSON allows arbitrary length fractions.
      pos = fractionEnd;
    }

    // Optional exponent.
    if (pos < end && 'e' == (jsonish.charAt(pos) | 32)) {
      ++pos;
      if (pos < end) {
        switch (jsonish.charAt(pos)) {
          // JSON allows explicit + in exponent but not for number as a whole.
          case '+': case '-': ++pos; break;
          default: break;
        }
      }
      // JSON allows leading zeros on exponent part.
      int expEnd = endOfDigitRun(pos, end);
      if (expEnd == pos) {
        insert(pos, '0');
      }
      pos = expEnd;
    }
    if (pos != end) {
      elide(pos, end);
    }
  }
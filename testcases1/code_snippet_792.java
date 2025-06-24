@Override
        public String toString() {
//            StringBuilder buf = new StringBuilder();
//            for (long l : past5) {
//                if(buf.length()>0)  buf.append(',');
//                buf.append(l);
//            }
//            return buf.toString();
            int fc = failureCount();
            if(fc>0)
                return Util.wrapToErrorSpan(Messages.ResponseTimeMonitor_TimeOut(fc));
            return getAverage()+"ms";
        }
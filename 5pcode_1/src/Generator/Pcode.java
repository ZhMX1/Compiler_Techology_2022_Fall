package Generator;

public class Pcode {
    private PcodeType type;
    private Object value1 = null;
    private Object value2 = null;

    public Pcode(PcodeType type) {
        this.type = type;
    }

    public Pcode(PcodeType type, Object value1) {
        this.type = type;
        this.value1 = value1;
    }

    public Pcode(PcodeType type, Object value1, Object value2) {
        this.type = type;
        this.value1 = value1;
        this.value2 = value2;
    }

    public void setValue2(Object value2) {
        this.value2 = value2;
    }

    public PcodeType getType() {
        return type;
    }

    public Object getValue1() {
        return value1;
    }

    public Object getValue2() {
        return value2;
    }

    @Override
    public String toString() {
        if (type.equals(PcodeType.LABEL)) {
            return value1.toString() + ": ";
        }
        if (type.equals(PcodeType.FUNC)) {
            return "FUNC @" + value1.toString() + ":";
        }
        if (type.equals(PcodeType.CALL)) {
            return "$" + value1.toString();
        }
        if (type.equals(PcodeType.PRINT)) {
            return type + " " + value1;
        }
        String a = value1 != null ? value1.toString() : "";
        String b = value2 != null ? ", " + value2.toString() : "";
        return type + " " + a + b;
    }
}

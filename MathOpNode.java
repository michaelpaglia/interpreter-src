public class MathOpNode extends Node {
    Node left;
    Node right;
    public OPERATION operation;

    public enum OPERATION {
        ADD {
            public String toString() {
                return "+";
            }
        },
        SUBTRACT {
            public String toString() {
                return "-";
            }
        },
        MULTIPLY {
            public String toString() {
                return "*";
            }
        },
        DIVIDE {
            public String toString() {
                return "/";
            }
        },
        MODULO {
            public String toString() { return "%"; }
        },
        ISGREATERTHAN {
            public String toString() { return ">"; }
        },
        ISLESSTHAN {
            public String toString() { return "<"; }
        },
        ISGREATERTHANOREQUALTO {
            public String toString() { return ">="; }
        },
        ISLESSTHANOREQUALTO {
            public String toString() { return "<="; }
        },
        ISNOTEQUALTO {
            public String toString() { return "!="; }
        },
        ISEQUALTO {
            public String toString() { return "=="; }
        }
    }

    MathOpNode(OPERATION p_operation, Node p_left, Node p_right) {
        this.left = p_left;
        this.right = p_right;
        this.operation = p_operation;
    }

    public String toString() {
        return "MathOpNode(" + operation + " " + left + " : " + right + ")";
    }
}

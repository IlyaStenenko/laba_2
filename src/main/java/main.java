import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;



public class main {

    public static void main(String... A)
    {
        String expressionText;
        Scanner in = new Scanner(System.in);
        expressionText = in.nextLine();
        List<Lexeme> lexemes = lexeme_parsing(expressionText);//Парсим строку на символы
        LexemeBuffer lexemeBuffer = new LexemeBuffer(lexemes);
        System.out.println(expr(lexemeBuffer));//Считаем выражение
    }

    public enum LexemeValue //Типы значений которые могут встретиться в выражении
    {
        l_bracket, r_bracket, plus, minus, mult, div, number, EOF;
    }


    public static class Lexeme {
        LexemeValue type;
        String value;

        public Lexeme(LexemeValue type, String value) {
            this.type = type;
            this.value = value;
        }//Конструктор

        public Lexeme(LexemeValue type, Character value) {
            this.type = type;
            this.value = value.toString();
        }//Конструктор если строка задается через char

        @Override//перегружаем toString для вывода
        public String toString() {
            return "Lexeme{" +
                    "type=" + type +
                    ", value='" + value + '\'' +
                    '}';
        }
    }
    public static class LexemeBuffer {
        private int pos;//позиция в строке лексем

        public List<Lexeme> lexemes;// все распарсенные лексемы

        public LexemeBuffer(List<Lexeme> lexemes) {
            this.lexemes = lexemes;
        }

        public Lexeme next() {
            return lexemes.get(pos++);
        }

        public void back() {
            pos--;
        }

        public int getPos() {
            return pos;
        }
    }

    public static List<Lexeme> lexeme_parsing(String expText) {//собственно сам парсинг
        ArrayList<Lexeme> lexemes = new ArrayList<>();
        int pos = 0;
        while (pos< expText.length()) {
            char c = expText.charAt(pos);
            switch (c) {
                case '(':
                    lexemes.add(new Lexeme(LexemeValue.l_bracket, c));
                    pos++;
                    continue;
                case ')':
                    lexemes.add(new Lexeme(LexemeValue.r_bracket, c));
                    pos++;
                    continue;
                case '+':
                    lexemes.add(new Lexeme(LexemeValue.plus, c));
                    pos++;
                    continue;
                case '-':
                    lexemes.add(new Lexeme(LexemeValue.minus, c));
                    pos++;
                    continue;
                case '*':
                    lexemes.add(new Lexeme(LexemeValue.mult, c));
                    pos++;
                    continue;
                case '/':
                    lexemes.add(new Lexeme(LexemeValue.div, c));
                    pos++;
                    continue;
                default:
                    if (c <= '9' && c >= '0') {//вводим число как последовательность цифр
                        StringBuilder number_ = new StringBuilder();
                        do {
                            number_.append(c);
                            pos++;
                            if (pos >= expText.length()) {
                                break;
                            }
                            c = expText.charAt(pos);
                        } while (c <= '9' && c >= '0');
                        lexemes.add(new Lexeme(LexemeValue.number, number_.toString()));
                    } else {
                        if (c != ' ') {
                            throw new RuntimeException("Непонятный символ: " + c);
                        }
                        pos++;
                    }
            }
        }
        lexemes.add(new Lexeme(LexemeValue.EOF, ""));
        return lexemes;
    }

    //Вот такая идея решения
    //Спукаемся запуская expr в нем находим plusminus в нем находим если есть multdiv а в нем если есть brackets and numbers и
    //expr(plusminus)
    //plusminus(multdiv)
    //multdiv(brackets and numbers)
    //brackets and numbers



    public static int expr(LexemeBuffer lexemes) {
        Lexeme lexeme = lexemes.next();
        if (lexeme.type == LexemeValue.EOF) {
            return 0;
        } else {
            lexemes.back();
            return plusminus(lexemes);
        }
    }

    public static int plusminus(LexemeBuffer lexemes) {
        int value = multdiv(lexemes);
        while (true) {
            Lexeme lexeme = lexemes.next();
            switch (lexeme.type) {
                case plus:
                    value += multdiv(lexemes);
                    break;
                case minus:
                    value -= multdiv(lexemes);
                    break;
                case EOF:
                case r_bracket:
                    lexemes.back();
                    return value;
                default:
                    throw new RuntimeException("Непонятое значение " + lexeme.value
                            + " на позиции " + lexemes.getPos());
            }
        }
    }

    public static int multdiv(LexemeBuffer lexemes) {
        int value = brackets_and_numbers(lexemes);
        while (true) {
            Lexeme lexeme = lexemes.next();
            switch (lexeme.type) {
                case mult:
                    value *= brackets_and_numbers(lexemes);
                    break;
                case div:
                    value /= brackets_and_numbers(lexemes);
                    break;
                case EOF:
                case r_bracket:
                case plus:
                case minus:
                    lexemes.back();
                    return value;
                default:
                    throw new RuntimeException("Непонятое значение  " + lexeme.value
                            + " на позиции: " + lexemes.getPos());
            }
        }
    }

    public static int brackets_and_numbers(LexemeBuffer lexemes) {
        Lexeme lexeme = lexemes.next();
        switch (lexeme.type) {
            case number:
                return Integer.parseInt(lexeme.value);
            case l_bracket:
                int value = plusminus(lexemes);
                lexeme = lexemes.next();
                if (lexeme.type != LexemeValue.r_bracket) {
                    throw new RuntimeException("Непонятое значение: " + lexeme.value
                            + " на позиции: " + lexemes.getPos());
                }
                return value;
            default:
                throw new RuntimeException("Непонятое значение: " + lexeme.value
                        + " на позиции: " + lexemes.getPos());
        }
    }







}

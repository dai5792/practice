// Panel クラスの使用例
import java.awt.*;
import java.awt.event.*;

public class Calculator extends Frame implements ActionListener {
    TextField txt1;
    String input = "";
    double currentResult = 0;
    char operator = ' ';

    public static void main(String[] args) {
        Calculator cal = new Calculator();
    }

    Calculator() {
        super("Calculator");
        setSize(300, 200);
        setLayout(new BorderLayout());

        txt1 = new TextField();
        add(txt1, BorderLayout.NORTH);

        Panel p_center = new Panel();
        p_center.setLayout(new GridLayout(4, 4));

        // ボタンの配置とリスナ登録
        addButton(p_center, "7");
        addButton(p_center, "8");
        addButton(p_center, "9");
        addButton(p_center, "+");

        addButton(p_center, "4");
        addButton(p_center, "5");
        addButton(p_center, "6");
        addButton(p_center, "-");

        addButton(p_center, "1");
        addButton(p_center, "2");
        addButton(p_center, "3");
        addButton(p_center, "×");

        addButton(p_center, "0");
        addButton(p_center, ".");
        addButton(p_center, "=");
        addButton(p_center, "÷");

        add(p_center, BorderLayout.CENTER);

        Panel p_south = new Panel();
        p_south.setLayout(new GridLayout(1, 1));
        Button btClr = new Button("Clear");
        btClr.addActionListener(this);
        p_south.add(btClr);

        add(p_south, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void addButton(Panel panel, String label) {
        Button button = new Button(label);
        button.addActionListener(this);
        panel.add(button);
    }

    public void actionPerformed(ActionEvent e) {
        Button btn = (Button) e.getSource();

        if (btn.getLabel().equals("Clear")) {
            // Clearボタンが押されたとき
            input = "";
            currentResult = 0;
            operator = ' ';
            updateTextField();
        } else if (btn.getLabel().equals("=")) {
            // = ボタンが押されたとき
            calculateResult();
        } else if (isOperator(btn.getLabel().charAt(0))) {
            // 演算子ボタンが押されたとき
            calculateResult();  // これまでの計算を実行
            operator = btn.getLabel().charAt(0);  // 新しい演算子を取得
            input = "";
            updateTextField();
        } else {
            // 数字や小数点ボタンが押されたとき
            input += btn.getLabel();
            updateTextField();
        }
    }

    private void calculateResult() {
        try {
            // 1. テキストフィールドに入力された文字列を double 型に変換
            double secondOperand = Double.parseDouble(input);

            // 2. 現在の計算結果と演算子を用いて計算を実行
            switch (operator) {
                case '+':
                    currentResult += secondOperand;
                    break;
                case '-':
                    currentResult -= secondOperand;
                    break;
                case '×':
                    currentResult *= secondOperand;
                    break;
                case '÷':
                    if (secondOperand != 0) {
                        currentResult /= secondOperand;
                    } else {
                        // ゼロで割るエラーの場合
                        System.out.println("Error: Division by zero");
                    }
                    break;
                default:
                    // 演算子が設定されていない場合、現在の入力を結果とする
                    currentResult = secondOperand;
            }

            // 3. 結果を文字列に変換してテキストフィールドに表示
            input = String.valueOf(currentResult);
            operator = ' ';
            updateTextField();
        } catch (NumberFormatException e) {
            // 例外処理: 数字に変換できない場合（非数値または形式が正しくない場合）
            System.out.println("Error: Invalid input");
            input = "";
            updateTextField();
        }
    }

    private void updateTextField() {
        // テキストフィールドを入力内容で更新
        txt1.setText(input);
    }

    private boolean isOperator(char c) {
        // 渡された文字が演算子かどうかを判定
        return c == '+' || c == '-' || c == '×' || c == '÷';
    }
}

/**
 * Simple Drawing Application
 * 簡単なお絵かきソフト
 * ・フリーハンド，直線，四角，楕円の描画機能
 * ・四角と楕円は左下方向のみ
 * ・色などの変更機能は無し
 *
 * @author fukai
 */
import java.awt.*;
import java.awt.event.*;


public class Drawing extends Frame implements ActionListener {
  // ■ フィールド変数
  Button bt1, bt2, bt3, bt4, bt5, bt6, bt7, bt8; // フレームに配置するボタンの宣言
  Panel  pnl, pn2;                // ボタン配置用パネルの宣言
  MyCanvas mc;               // 別途作成した MyCanvas クラス型の変数の宣言
  Scrollbar redScrollbar, greenScrollbar, blueScrollbar, lineWidthScrollbar;  
  int selectedRed = 0, selectedGreen = 0, selectedBlue = 0;
  private int lineWidth = 1;

  // ■ main メソッド（スタート地点）
  public static void main(String [] args) {
    Drawing da = new Drawing(); 
  }

  // ■ コンストラクタ
  Drawing() {
    super("Drawing Appli");
    this.setSize(700, 600); 

    pnl = new Panel();       // Panel のオブジェクト（実体）を作成
    pn2 = new Panel();
    mc = new MyCanvas(this); // mc のオブジェクト（実体）を作成

    this.setLayout(new BorderLayout(10, 10)); // レイアウト方法の指定
    this.add(pnl, BorderLayout.EAST);         // 右側に パネルを配置
    this.add(mc,  BorderLayout.CENTER);       // 左側に mc （キャンバス）を配置
                                         // BorerLayout の場合，West と East の幅は
                                         // 部品の大きさで決まる，Center は West と East の残り幅
    pnl.setLayout(new GridLayout(9,1));  // ボタンを配置するため，９行１列のグリッドをパネル上にさらに作成
    bt1 = new Button("Free Hand"); bt1.addActionListener(this); pnl.add(bt1);// ボタンを順に配置
    bt2 = new Button("Line");      bt2.addActionListener(this); pnl.add(bt2);
    bt3 = new Button("Rectangle"); bt3.addActionListener(this); pnl.add(bt3);
    bt4 = new Button("Oval");      bt4.addActionListener(this); pnl.add(bt4);
    bt5 = new Button("fillRect");  bt5.addActionListener(this); pnl.add(bt5);
    bt6 = new Button("fillOval");  bt6.addActionListener(this); pnl.add(bt6);
    bt7 = new Button("Clear");     bt7.addActionListener(this); pnl.add(bt7);
    bt8 = new Button("elaser");    bt8.addActionListener(this); pnl.add(bt8);

    pn2.setLayout(new GridLayout(4, 1));
    redScrollbar = createScrollbar(Scrollbar.HORIZONTAL);
    greenScrollbar = createScrollbar(Scrollbar.HORIZONTAL);
    blueScrollbar = createScrollbar(Scrollbar.HORIZONTAL);
    lineWidthScrollbar = createScrollbar2(Scrollbar.HORIZONTAL);
    
    pn2.add(redScrollbar);
    pn2.add(greenScrollbar);
    pn2.add(blueScrollbar);
    pn2.add(lineWidthScrollbar);

    this.add(pn2, BorderLayout.NORTH);

    this.setVisible(true); //可視化
  }
  

  // ■ メソッド
  // ActionListener を実装しているため、例え内容が空でも必ず記述しなければならない
  public void actionPerformed(ActionEvent e){ // フレーム上で生じたイベントを e で取得
    if (e.getSource() == bt1)      // もしイベントが bt1 で生じたなら
      mc.mode=1;                   // モードを１に
    else if (e.getSource() == bt2) // もしイベントが bt2 で生じたなら
      mc.mode=2;                   // モードを２に
    else if (e.getSource() == bt3) // もしイベントが bt3 で生じたなら
      mc.mode=3;                   // モードを３に
    else if (e.getSource() == bt4) // もしイベントが bt4 で生じたなら
      mc.mode=4;                   // モードを４に
    else if (e.getSource() == bt5)
      mc.mode=5;
    else if (e.getSource() == bt6)
      mc.mode=6;
    else if (e.getSource() == bt7)
      mc.clearCanvas();
    else if (e.getSource() == bt8)
      mc.mode=8;
  }

  public Color getSelectedColor() {
    return new Color(selectedRed, selectedGreen, selectedBlue);
  }

  public BasicStroke getStroke(){
    return new BasicStroke(lineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
  }

  private Scrollbar createScrollbar(int orientation) {
    Scrollbar scrollbar = new Scrollbar(orientation, 0, 1, 0, 256);
    scrollbar.addAdjustmentListener(e -> {
        // スクロールバーの値が変更されたときの処理をここに追加
        int redValue = redScrollbar.getValue();
        int greenValue = greenScrollbar.getValue();
        int blueValue = blueScrollbar.getValue();

        // ここで選択された色に対する処理を実行
        Color selectedColor = new Color(redValue, greenValue, blueValue);
        // 例えば、選択された色をコンソールに表示する
        System.out.println("Selected Color: " + selectedColor);

        mc.updateSelectedColor(redValue, greenValue, blueValue);
    });
    return scrollbar;
  }

  private Scrollbar createScrollbar2(int orientation) {
    Scrollbar scrollbar = new Scrollbar(orientation, 1, 1, 1, 10);
    scrollbar.addAdjustmentListener(e -> {
        // スクロールバーの値が変更されたときの処理をここに追加
        lineWidth = lineWidthScrollbar.getValue();
        System.out.println("Line Width: " + lineWidth);
        mc.updateLineWidth(lineWidth);
    });
    return scrollbar;
  }

  public void setSelectedColor(int red, int green, int blue) {
    selectedRed = red;
    selectedGreen = green;
    selectedBlue = blue;
  }

  public void setLineWidth(int lineWidth) {
    this.lineWidth = lineWidth;
  }

  // Drawing クラスにスクロールバーの変更を検知するリスナークラスを追加
  class ScrollbarListener implements AdjustmentListener {
    public void adjustmentValueChanged(AdjustmentEvent e) {
      selectedRed = redScrollbar.getValue();
      selectedGreen = greenScrollbar.getValue();
      selectedBlue = blueScrollbar.getValue();

      // MyCanvas クラスに描画色の変更を通知
      mc.updateSelectedColor(selectedRed, selectedGreen, selectedBlue);
    }
  }
}

/**
 * Extended Canvas class for DrawingApli
 * [各モードにおける処理内容]
 * 1: free hand 
 *      pressed -> set x, y,  dragged  -> drawline & call repaint()
 * 2: draw line 
 *      pressed -> set x, y,  released -> drawline & call repaint()
 * 3: rect
 *      pressed -> set x, y,  released -> calc w, h & call repaint()
 * 4: circle
 *      pressed -> set x, y,  released -> calc w, h & call repaint()
 * 
 * @author fukai
 */
class MyCanvas extends Canvas implements MouseListener, MouseMotionListener {
  // ■ フィールド変数
  int x, y;   // mouse pointer position
  int px, py; // preliminary position
  int ow, oh; // width and height of the object
  int mode;   // drawing mode associated as below
  Image img = null;   // 仮の画用紙
  Graphics gc = null; // 仮の画用紙用のペン
  Dimension d; // キャンバスの大きさ取得用
  Drawing drawing;
  private int lineWidth2 = 1;

  // ■ コンストラクタ
  MyCanvas(Drawing drawing){
    this.drawing = drawing;
    mode=0;                       // initial value 
    this.setSize(500,400);        // キャンバスのサイズを指定
    addMouseListener(this);       // マウスのボタンクリックなどを監視するよう指定
    addMouseMotionListener(this); // マウスの動きを監視するよう指定
  }

  // ■ メソッド（オーバーライド）
  // フレームに何らかの更新が行われた時の処理
  public void update(Graphics g) {
    paint(g); // 下記の paint を呼び出す
  }

  // ■ メソッド（オーバーライド）
  public void paint(Graphics g) {
    Graphics2D g2d = (Graphics2D)gc;
    d = getSize();   // キャンバスのサイズを取得
    if (img == null) // もし仮の画用紙の実体がまだ存在しなければ
      img = createImage(d.width, d.height); // 作成
    if (gc == null)  // もし仮の画用紙用のペン (GC) がまだ存在しなければ
      gc = img.getGraphics(); // 作成

    gc.setColor(drawing.getSelectedColor());
    g2d.setStroke(drawing.getStroke());
    switch (mode){
    case 1: // モードが１の場合
      gc.drawLine(px, py, x, y); // 仮の画用紙に描画
      break;
    case 2: // モードが２の場合
      gc.drawLine(px, py, x, y); // 仮の画用紙に描画
      break;
    case 3: // モードが３の場合
      gc.drawRect(px, py, ow, oh); // 仮の画用紙に描画
      break;
    case 4: // モードが４の場合
      gc.drawOval(px, py, ow, oh); // 仮の画用紙に描画
      break;
    case 5:
      gc.fillRect(px, py, ow, oh);
      break;
    case 6:
      gc.fillOval(px, py, ow, oh);
      break;
    case 7:
      gc.setColor(Color.WHITE);
      gc.fillRect(0, 0, 700, 600);
      break;
    case 8:
      gc.setColor(Color.WHITE);
      gc.drawLine(px, py, x, y);
      break;
    }
    g.drawImage(img, 0, 0, this); // 仮の画用紙の内容を MyCanvas に描画
  }

  public void clearCanvas(){
    mode = 7;
    repaint();
  }

  public void updateSelectedColor(int red, int green, int blue) {
    drawing.setSelectedColor(red, green, blue);
    repaint();
  }

  public void updateLineWidth(int lineWidth) {
    lineWidth2 = lineWidth;
    drawing.setLineWidth(lineWidth2);
    repaint();
  }

  // ■ メソッド
  // 下記のマウス関連のメソッドは，MouseListener をインターフェースとして実装しているため
  // 例え使わなくても必ず実装しなければならない
  public void mouseClicked(MouseEvent e){}// 今回は使わないが、無いとコンパイルエラー
  public void mouseEntered(MouseEvent e){}// 今回は使わないが、無いとコンパイルエラー
  public void mouseExited(MouseEvent e){} // 今回は使わないが、無いとコンパイルエラー
  public void mousePressed(MouseEvent e){ // マウスボタンが押された時の処理
    switch (mode){
    case 1: // mode が１の場合，次の内容を実行する
    case 8:
      x = e.getX();
      y = e.getY();
      break;
    case 2: // mode が２もしくは
    case 3: // ３もしくは
    case 4: // ４の場合，次の内容を実行する
    case 5:
    case 6:
      px = e.getX();
      py = e.getY();
    }
  }
  public void mouseReleased(MouseEvent e){ // マウスボタンが離された時の処理
    switch (mode){
    case 2: // mode が２もしくは
    case 3: // ３もしくは
    case 4: // ４の場合，次の内容を実行する
    case 5:
    case 6:
      x = e.getX();
      y = e.getY();
      ow = Math.abs(x-px);
      oh = Math.abs(y-py);
      px = Math.min(x, px);
      py = Math.min(y, py);
      repaint(); // 再描画
    }
  }

  // ■ メソッド
  // 下記のマウス関連のメソッドは，MouseMotionListener をインターフェースとして実装しているため
  // 例え使わなくても必ず実装しなければならない
  public void mouseDragged(MouseEvent e){ // マウスがドラッグされた時の処理
    switch (mode){
    case 1: // mode が１の場合，次の内容を実行する
    case 8:
      px = x;
      py = y;
      x = e.getX();
      y = e.getY();
      repaint(); // 再描画
    }
  }
  public void mouseMoved(MouseEvent e){} // 今回は使わないが、無いとコンパイルエラー
}
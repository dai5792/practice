import java.awt.*;

public class JumpGame extends Frame implements Runnable{
    
    Thread th;
    GameMaster gm;

    public static void main(String[] args){
        new JumpGame();
    }

    JumpGame(){
        super("Jump Game"); //親クラスのコンストラクタを呼び出す
        int cw=640, ch=480; //キャンパスのサイズ
        this.setSize(cw+30, ch+40); //フレームのサイズを指定
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10)); //キャンパスをフレームに配置

        gm = new GameMaster(cw, ch); //GameMasterクラスのオブジェクトを生成
        this.add(gm); //キャンパスをフレームに配置
        this.setVisible(true); //可視化

        th = new Thread(this); //Threadクラスのオブジェクトを作成
        th.start(); //最後にスレッドをstartメソッドで開始

        requestFocusInWindow(); //フォーカスを得る
    }

    public void run(){
        try{
            while (true) {
                Thread.sleep(20); //ウィンドウを更新する前に指定時間だけ休止
                gm.repaint(); //再描画を要求する。　repaint()はupdate()を呼び出す
            }
        }
        catch (Exception e) {System.out.println("Exception: "+ e);}
    }
}
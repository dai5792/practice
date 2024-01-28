import java.awt.*;
import java.awt.event.*;

public class GameMaster extends Canvas implements KeyListener{

    Image buf; //仮の画面としてのbufferに使うオブジェクト
    Graphics buf_gc; //bufferのグラフィックスコンテキスト(gc)用オブジェクト
    Dimension d; //アプレットの大きさを管理するオブジェクト
    Image gameover;
    Image title;
    Image clear;
    Image background;

    private int imgW, imgH; //キャンパスの大きさ
    private int fldnum = 20;
    private long startTime;
    private long elapsedTime;
    private long timelimit = 45000; //ミリ秒
    private int mode = 2; // 0:ゲームオーバー 1:ゲームクリア 2:タイトル 3:ゲーム中

    Player ply;
    Field grd;
    Goal goal;
    Field fld[] = new Field[fldnum];

    GameMaster(int imgW, int imgH){
        this.imgW = imgW; //引数として取得した描画領域のサイズをローカルなプライベート変数に代入
        this.imgH = imgH; //引数として取得した描画領域のサイズをローカルなプライベート変数に代入
        setSize(imgW,imgH); //描画領域のサイズを設定

        addKeyListener(this);

        startTime = System.currentTimeMillis();

        ply = new Player(imgW,imgH);
        grd = new Field(0,450,640,15);
        goal = new Goal(100,75,5,5);
        for(int i=0;i<fldnum;i++)
            fld[i] = new Field(imgW, imgH);
        gameover = this.getToolkit().getImage("img00/Gameover.jpeg");
        title = this.getToolkit().getImage("img00/Gamestart.jpeg");
        clear = this.getToolkit().getImage("img00/clear.jpeg");
        background = this.getToolkit().getImage("img00/background2.png");
    }

    public void addNotify(){
        super.addNotify();
        buf = createImage(imgW,imgH); //bufferを画面と同サイズで作成
        buf_gc = buf.getGraphics();
    }

    private void resetGame() {
        mode = 2; // タイトル画面
        startTime = 0; // タイマーの初期化
        ply = new Player(imgW, imgH); // プレイヤーオブジェクトの初期化
        grd = new Field(0, 450, 640, 15); // フィールドオブジェクトの初期化
        goal = new Goal(100, 75, 5, 5); // ゴールオブジェクトの初期化
        for (int i = 0; i < fldnum; i++)
            fld[i] = new Field(imgW, imgH); // フィールド配列の初期化

        startTime = System.currentTimeMillis();
    }


    public void paint(Graphics g){
        buf_gc.setColor(Color.white); //gcの色を城に
        buf_gc.fillRect(0, 0, imgW, imgH); //gcを使って白の四角を描く

        switch (mode) {
        case 0://ゲームオーバー
            buf_gc.drawImage(gameover, 0, 0, imgW, imgH, this);
            //System.out.println(mode);
            break;
        case 1://クリア
            buf_gc.drawImage(clear, 0, 0, imgW, 380, this);
            buf_gc.setColor(Color.black);

            Font origiFont = buf_gc.getFont();
            Font largeFont = origiFont.deriveFont(30.0f);
            buf_gc.setFont(largeFont);

            buf_gc.drawString("Score: " + ply.getScore(), 250, 400);

            buf_gc.setFont(origiFont);
            //System.out.println(mode);
            break;
        case 2://タイトル
            buf_gc.drawImage(title, 0, 0, imgW, imgH, this);
            break;
        case 3:
            buf_gc.drawImage(background, 0, 0, imgW, imgH, this);
            elapsedTime = System.currentTimeMillis() - startTime;
            long remainingTime = Math.max(0,timelimit-elapsedTime);

            if(ply.gflag){
                mode = 1;
            }

            if(elapsedTime >= timelimit){
                //ゲームオーバー
                mode = 0;
                return;
            }

            buf_gc.setColor(Color.black);
            buf_gc.drawString("Time: " + remainingTime / 1000 + "s", 580, 30);

            for(int i=0;i<fldnum;i++)
                ply.collisionCheck(fld[i]);
            ply.collisionCheck(grd);
            ply.collisionCheck(goal, mode);

            grd.move(buf_gc, imgW, imgH);
            for (int i = 0; i < fldnum; i++) {
                fld[i].move(buf_gc, imgW, imgH);
            }
            ply.move(buf_gc, imgW, imgH);
            goal.move(buf_gc, imgW, imgH);
        }        
        g.drawImage(buf, 0, 0, this); //表の画用紙に裏の画用紙(buffer)の内容を貼り付ける
    }

    public void update(Graphics gc){
        paint(gc);
    }

    //メソッド(KeyListener)
    public void keyTyped(KeyEvent ke){
    } 

    public void keyPressed(KeyEvent ke){
        int cd = ke.getKeyCode();
        switch (cd){
        case KeyEvent.VK_LEFT:
            if(!ply.lflag){
                ply.lflag = true;
                ply.dx = -3;
            }
            break;
        case KeyEvent.VK_RIGHT:
            if(!ply.rflag){
                ply.rflag = true;
                ply.dx = 3;
            }
            break;
        case KeyEvent.VK_SPACE:
            if(!ply.sflag){
                ply.sflag = true;
                ply.up = 1.0;
                ply.down = 0.5;
            }
            break;
        case KeyEvent.VK_ENTER:
            if(mode == 0 || mode == 1){
                mode = 2;
                resetGame();
            }else if(mode == 2){
                mode = 3;
            }
            break;
        }
    }

    public void keyReleased(KeyEvent ke){
        int cd = ke.getKeyCode();
        switch (cd){
        case KeyEvent.VK_LEFT:
            //ply.lflag = false;
            break;
        case KeyEvent.VK_RIGHT:
            //ply.rflag = false;
            break;
        case KeyEvent.VK_SPACE:
            ply.up = 0.0;
            ply.down = 0.5;
            break;   
        case KeyEvent.VK_ENTER:
            break;
        }
    }
}
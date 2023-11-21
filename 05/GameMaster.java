import java.awt.*;
import java.awt.event.*;

public class GameMaster extends Canvas implements KeyListener{
    //フィールド変数
    Image buf; //仮の画面としてのbufferに使うオブジェクト
    Graphics buf_gc; //bufferのグラフィックスコンテキスト(gc)用オブジェクト
    Dimension d; //アプレットの大きさを管理するオブジェクト
    private int imgW, imgH; //キャンパスの大きさ
    private Image backgroundImage;
    private Image gameover;
    private Image title;

    private int enmyAnum = 20; //敵Aの数
    private int enmyBnum = 5; //敵Bの数
    private int ftrBltNum = 10; //自機の弾の数
    private int mode = -1; //-1:タイトル画面, -2:ゲームオーバー, 1~ ゲームステージ
    private int i, j;
    private int scrollY = 0;
    private int scrollSpeed = 2;

    Fighter ftr; //自機
    FighterBullet ftrBlt[] = new FighterBullet[ftrBltNum]; //自機の弾
    EnemyA enmyA[] = new EnemyA[enmyAnum]; //敵機A
    EnemyB enmyB[] = new EnemyB[enmyBnum]; //敵機B
    EnemyBullet enemyBullet[] = new EnemyBullet[enmyAnum]; 
 
    //コンストラクタ
    /*
     * ゲームの初期設定
     * ・描画領域(Image)とGC(Graphics)の作成
     * ・敵,自機,弾オブジェクトの作成
     */
    GameMaster(int imgW, int imgH){
        this.imgW = imgW; //引数として取得した描画領域のサイズをローカルなプライベート変数に代入
        this.imgH = imgH; //引数として取得した描画領域のサイズをローカルなプライベート変数に代入
        setSize(imgW,imgH); //描画領域のサイズを設定

        addKeyListener(this);

        ftr = new Fighter(imgW,imgH); //自機のオブジェクトをを実際に作成
        for(i=0;i<ftrBltNum;i++) //自機弾のオブジェクトを実際に作成
            ftrBlt[i] = new FighterBullet();
        for(i=0;i<enmyAnum;i++) //敵Aのオブジェクトを実際に作成
            enmyA[i] = new EnemyA(imgW,imgH);
        for(i=0;i<enmyBnum;i++)
            enmyB[i] = new EnemyB(imgW,imgH);
        for(i=0;i<enmyAnum;i++)    
            enemyBullet[i] = new EnemyBullet();

        loadImage("img05/background.jpg");  // 背景画像の読み込み
        gameover = this.getToolkit().getImage("img05/Gameover.jpeg");
        title = this.getToolkit().getImage("img05/Gamestart.jpeg");
    }

    //メソッド
    //コンストラクタ内でcreateImageを行うとpeerの関連で
    //nullpointer exceptionが返ってくる問題を回避するために必要
    public void addNotify(){
        super.addNotify();
        buf = createImage(imgW,imgH); //bufferを画面と同サイズで作成
        buf_gc = buf.getGraphics();
    }

    // 画像の読み込み
    private void loadImage(String imagePath) {
        try {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            backgroundImage = toolkit.getImage(imagePath);
            MediaTracker tracker = new MediaTracker(this);
            tracker.addImage(backgroundImage, 0);
            tracker.waitForAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //メソッド(Canvas)
    public void paint(Graphics g){
        buf_gc.setColor(Color.black); //gcの色を城に
        buf_gc.fillRect(0, 0, imgW, imgH); //gcを使って白の四角を描く
        switch (mode) {
        case -2: //ゲームオーバー画面
            buf_gc.setColor(Color.black);
            //buf_gc.drawString("  == Game over ==  ", imgW/2-80, imgH/2-20);
            buf_gc.drawImage(gameover, 75, 75, this);
            //buf_gc.drawString("   Hit ENTER key   ", imgW/2-80, imgH/2+20);
            break;
        case -1: //タイトル画面
            buf_gc.setColor(Color.black);
            //buf_gc.drawString(" == Shooting Game Title == ", imgW/2-80, imgH/2-20);
            //buf_gc.drawString("Hit ENTER bar to start game", imgW/2-80, imgH/2+20);
            buf_gc.drawImage(title, 75, 75, this);
            break;        
        case 0: //ゲーム中
            // 背景画像の描画
            //buf_gc.drawImage(backgroundImage, 0, 0, this);
            // 縦方向にスクロール
            scrollY += scrollSpeed;
            if (scrollY > imgH) {
                scrollY = 0;
            }
            // 背景画像の描画
            buf_gc.drawImage(backgroundImage, 0, -scrollY, imgW, imgH - scrollY, 0, 0, imgW, imgH, this);
            buf_gc.drawImage(backgroundImage, 0, imgH - scrollY, imgW, imgH * 2 - scrollY, 0, 0, imgW, imgH, this);

            buf_gc.setColor(Color.white);
            buf_gc.drawString("HP:" + ftr.hp, 20,20);
            //ランダムに敵を生成
            makeEnmyA: if(Math.random() < 0.1){ //10%の確立で一匹生成
                for(i=0;i<enmyAnum;i++){
                    if(enmyA[i].hp == 0){
                        enmyA[i].revive(imgW,imgH);
                        break makeEnmyA;
                    }
                }
            }

            makeEnmyB: if(Math.random() < 0.05){
                for(i=0;i<enmyBnum;i++){
                    if(enmyB[i].hp == 0){
                        enmyB[i].revive(imgW,imgH);
                        break makeEnmyB;                    }
                }
            }

            //自分の弾を発射
            if(ftr.sflag == true && ftr.delaytime == 0){ //もしスペースキーが押されている&待ち時間がゼロ
                for(i=0;i<ftrBltNum;i++){                //全部の弾に関して前から探索して
                    if(ftrBlt[i].hp == 0){               //非アクティブの弾があれば
                        ftrBlt[i].revive(ftr.x, ftr.y);  //自機から弾を発射して
                        ftr.delaytime = 5;               //自機の弾発射待ち時間を元に戻して
                        break;                           //for loopを抜ける
                    }
                }
            } else if (ftr.delaytime > 0)                //弾を発射できない場合は
              ftr.delaytime--;                           //待ち時間を1減らす

            // 敵機が一定の条件で弾を発射する
            for(i=0;i<enmyAnum;i++){
                if(enmyA[i].hp > 0){
                    if (Math.random() < 0.01) { // 例えば10%の確率で発射
                        enemyBullet[i].revive(enmyA[i].x, enmyA[i].y);
                    }
                }
            }

            //各オブジェクト間の衝突チェック
            for (i=0;i<enmyAnum; i++){                   //全ての敵Aに関し
                if (enmyA[i].hp > 0) {                   //敵が生きていたら
                    ftr.collisionCheck(enmyA[i]);        //自機との衝突チェック
                    if(enemyBullet[i].hp > 0)
                        ftr.collisionCheck(enemyBullet[i]);
                    for (j=0;j<ftrBltNum;j++){           //全ての自弾に関して
                        if(ftrBlt[j].hp > 0)             //自弾が生きていいたら
                            ftrBlt[j].collisionCheck(enmyA[i]); //自弾との衝突チェック
                    }
                }
            }    

            for (i=0;i<enmyBnum; i++){                   //全ての敵Bに関し
                if (enmyB[i].hp > 0) {                   //敵が生きていたら
                    ftr.collisionCheck(enmyB[i]);        //自機との衝突チェック
                    for (j=0;j<ftrBltNum;j++){           //全ての自弾に関して
                        if(ftrBlt[j].hp > 0)             //自弾が生きていいたら
                            enmyB[i].collisionCheck(ftrBlt[j]); //自弾との衝突チェック
                    }
                }
            }    

            //自機の生死を判断
            if (ftr.hp<1)
                mode = -2; //ゲーム終了
                
            //オブジェクトの描画＆移動
            for (i=0;i<enmyAnum;i++)
                enmyA[i].move(buf_gc, imgW, imgH);
            for (i=0;i<enmyBnum;i++)
                enmyB[i].move(buf_gc, imgW, imgH);
            for (i=0;i<ftrBltNum;i++)
                ftrBlt[i].move(buf_gc, imgW, imgH);
            ftr.move(buf_gc, imgW, imgH);
            for (i=0;i<enmyAnum;i++)
                enemyBullet[i].move(buf_gc, imgW, imgH);

            //状態チェック
            for (i=0;i<enmyAnum;i++){
                System.out.print(enmyA[i].hp + " ");
            }
            System.out.println("");
        }
        g.drawImage(buf, 0, 0, this); //表の画用紙に裏の画用紙(buffer)の内容を貼り付ける
    }


    //メソッド(Canvas)
    public void update(Graphics gc){
        paint(gc);
    }

    //メソッド(KeyListener)
    public void keyTyped(KeyEvent ke){
    } //今回は使わないが実装は必要

    public void keyPressed(KeyEvent ke){
        int cd = ke.getKeyCode();
        switch (cd){
        case KeyEvent.VK_LEFT:
            ftr.lflag = true;
            break;
        case KeyEvent.VK_RIGHT:
            ftr.rflag = true;
            break;
        case KeyEvent.VK_UP:
            ftr.uflag = true;
            break;
        case KeyEvent.VK_DOWN:
            ftr.dflag = true;
            break;
        case KeyEvent.VK_SPACE:
            ftr.sflag = true;
            break;
        case KeyEvent.VK_ENTER:
            //eflag = true;
            this.mode++;
            ftr.hp = 10;
            break;
        }
    }

    public void keyReleased(KeyEvent ke){
        int cd = ke.getKeyCode();
        switch (cd){
        case KeyEvent.VK_LEFT:
            ftr.lflag = false;
            break;
        case KeyEvent.VK_RIGHT:
            ftr.rflag = false;
            break;
        case KeyEvent.VK_UP:
            ftr.uflag = false;
            break;
        case KeyEvent.VK_DOWN:
            ftr.dflag = false;
            break;
        case KeyEvent.VK_SPACE:
            ftr.sflag = false;
            break;
        case KeyEvent.VK_ENTER:
            //eflag = false;
            break;    
        }
    }
}


package banking;

import java.util.Random;

public class Card {
    protected String bin;
    protected String pin;
    protected int balance;
    Random random = new Random();


    public Card() {

        this.bin = generateBin();
        this.pin = generatePin();
        this.balance = 0;
    }

    String generatePin(){
        String p = "";
        for(int i = 0; i<4; i++){
            p += (String.valueOf(random.nextInt(10)));

        }
        return p;
    }

    String generateBin(){
        String b = "";
        Luhn l = new Luhn();
        int[] nums = {4,0,0,0,0,0,1,1,1,1,1,1,1,1,1};
        for(int i = 6; i<15; i++){
            nums[i] = random.nextInt(10);
        }
        for(int i = 0; i<15; i++){
            b += String.valueOf(nums[i]);
        }

        int c = l.luhnAlgorithm(b);

        b += String.valueOf(c);
        return b;
    }

    String getBin(){
        return this.bin;
    }

    String getPin(){
        return this.pin;
    }

}

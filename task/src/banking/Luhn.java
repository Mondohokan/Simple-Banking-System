package banking;

public class Luhn {

    public int luhnAlgorithm(String nums){

        int[] luhnArr = new int[nums.length()];
        int sum = 0;
        for(int i = 0; i<15; i++){
            luhnArr[i] = Integer.parseInt(nums.split("")[i]);
            if(i%2 == 0){
                luhnArr[i] *= 2;
            }
            if(luhnArr[i] > 9){
                luhnArr[i] -= 9;
            }
            sum += luhnArr[i];
        }

        int checksum = sum%10 != 0 ? 10 - sum%10 : 0;

        return checksum;
    }

    public boolean adheresToLuhn(String nums) {
        int[] luhnArr = new int[nums.length()];
        int sum = 0;
        for(int i = 0; i<luhnArr.length-1; i++){
            luhnArr[i] = Integer.parseInt(nums.split("")[i]);
            if(i%2 == 0){
                luhnArr[i] *= 2;
            }
            if(luhnArr[i] > 9){
                luhnArr[i] -= 9;
            }
            sum += luhnArr[i];
        }
        int checksum = sum%10 != 0 ? 10 - sum%10 : 0;
        luhnArr[luhnArr.length-1] = Integer.parseInt(nums.split("")[luhnArr.length-1]);
        if (checksum == luhnArr[luhnArr.length - 1]){
            return true;
        } else {
            return false;
        }
    }
}

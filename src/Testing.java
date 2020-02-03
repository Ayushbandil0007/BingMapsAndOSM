//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by Ayush Bandil on 28/1/2020.
// */
//public class Testing {
//
//    public static void main(String[] args) {
//        List<Integer> numbers = new ArrayList<>();
//        numbers.add(10);
//        numbers.add(6);
//        numbers.add(2);
//        numbers.add(3);
//        numbers.add(7);
//        numbers.add(1);
//        numbers.add(2);
//
//        System.out.println(minimumOperatiions(numbers));
//    }
//
//    private static long minimumOperatiions(List<Integer> numbers) {
//        if (numbers.size() == 0) {
//            return 0;
//        } else if (numbers.size() == 1) {
//            return 1;
//        }
//        int total = 0;
//        List<Integer> list = new ArrayList<>();
//
//        for (int i = 0; i < numbers.size(); i++) {
//            int size = list.size();
//            int position = getInsertionPosition(list, numbers.get(i));
//            list.add(position, numbers.get(i));
//            System.out.println(list.toString());
//            if (position > size / 2) {
//                position = size - position;
//            }
//            total += position * 2 + 1;
//        }
//        return total;
//    }
//
//    private static int getInsertionPosition(List<Integer> list, Integer integer) {
//        if (list.size() == 0) {
//            return 0;
//        } else if (list.get(0) >= integer) {
//            return 0;
//        } else if (list.get(list.size() - 1) <= integer) {
//            return list.size();
//        }
//        for (int i = 0; i < list.size() - 1; i++) {
//            if (list.get(i + 1) >= integer) {
//                return i + 1;
//            }
//        }
//        return list.size();
//    }
//}

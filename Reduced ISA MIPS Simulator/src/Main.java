import javax.sound.midi.SysexMessage;
import java.io.*;
import java.util.Scanner;
public class Main {
    private static String hexToBinary(String hex) {
        StringBuilder binary = new StringBuilder();
        // Process each hex digit
        for (int i = 0; i < hex.length(); i++) {
            // Convert to 4-bit binary
            String bin = String.format("%4s",
                    Integer.toBinaryString(
                            Integer.parseInt(hex.substring(i, i + 1), 16)
                    )
            ).replace(' ', '0');
            binary.append(bin);
        }
        return binary.toString();
    }

    public static String addHexadecimal(String a, String b) {
        // Convert hexadecimal strings to integers
        long intA = Long.parseLong(a, 16);
        long intB = Long.parseLong(b, 16);

        // Calculate sum and convert it back to hexadecimal
        long sum = intA + intB;
        return Long.toHexString(sum);
    }

    /*
     * We will use this method to change the address as needed for PC + 4.
     * First Scenario: PC + 4
     */
    public static String changeAddress(String currentAddress, File file, String scenario){
        //long i = 0;
        String preResult = "";
        StringBuilder result = new StringBuilder();
        if(scenario.equals("PC")){//perform PC + 4
            preResult = addHexadecimal(currentAddress,"00000004");
            for(int i = 0; i < 8 - preResult.length(); i++){
                result.append("0");
            }
            result.append(preResult);
        }
        return result.toString();
    }

    public static void main(String[] args) throws IOException {
        //Array full of registers with the index being used as register index for binary conversion!
        String[] registers = {"$zero", "$at", "$v0", "$v1", "$a0", "$a1", "$a2", "$a3", "$t0", "$t1", "$t2", "$t3", "$t4", "$t5", "$t6", "$t7",
                "$s0", "$s1", "$s2", "$s3", "$s4", "$s5", "$s6", "$s7", "$t8", "$t9", "$k0", "$k1", "$gp", "$sp", "$fp", "$ra"};
        String[] regValues = new String[32]; // will store register value
        for(int i = 0; i < regValues.length; i++){ // initialize array.
            regValues[i] = "0";
        }
        // WE WILL NEED to read .data first.
        // We will need to maintain arrays that keeps track of addresses and corresponding data.
        StringBuilder storeWord = new StringBuilder();
        StringBuilder pre_result = new StringBuilder();
        StringBuilder result = new StringBuilder();
        String[][] dataAddresses = new String[100][100];
        // first array is address. Second array is the text to be stored.
        int labelNums = 0;
        int lastStr = 0; //use it to keep track of the new addresses.
        //use this to store each data addresses.
        String DataBaseAddress = "10010000"; // in hexadecimal form. Base address for .data
        String textBaseAddress = "00400000"; // in hexadecimal form. Base address for .text

        File file = new File(args[1]); //read .data file
        BufferedReader br = new BufferedReader(new FileReader(file));

        // String variable that will store each line read by either .data or .text file
        String st;
        int textLength = 0;
        while((st = br.readLine()) != null){ // will read all of .data lines.
            if(st.equals("00000000")) break; // stop reading at terminating zeros.
            String binary = hexToBinary(st); // convert hex to binary.
            //Translate each line into 32 bits of binary
            //convert every 8 bits of binary. (4 characters in total).
            for(int i = 32; i > 0; i -= 8) {
                int toDec = Integer.parseInt(binary.substring(i - 8, i), 2);
                storeWord.append((char) toDec);
                textLength++;
                //detects when there is a null terminator.
                if (toDec == 0) {
                    textLength += lastStr;
                    // We will have to store the word into the data address array and recalculate the new address.
                    dataAddresses[0][labelNums] = DataBaseAddress;
                    dataAddresses[1][labelNums] = storeWord.toString();
                    labelNums++;

                    String addBinary = Integer.toBinaryString(textLength);
                    //converts the number of characters we have seen so far. (including null terminators)
                    //System.out.println("Addbinary:" + addBinary.length());
                    long temp1 = 0;
                    for (int y = 0; y < 4 - (addBinary.length() - 4); y++) { // ensure binary is 8 bits.( 1 byte)
                        pre_result.append("0");
                    }
                    //System.out.println("Pre result: " + pre_result.toString());
                    pre_result.append(addBinary);
                    //System.out.println("Pre result: " + pre_result.toString());
                    for (int y = 0; y < pre_result.length(); y += 4) {
                        //calculate the hexadecimal value of the binary value of the numbers of characters
                        //we have seen so far.
                        String binary1;
                        binary1 = pre_result.substring(y, y + 4);
                        temp1 = Long.parseLong(binary1, 2);
                        result.append(Long.toHexString(temp1));//Long.toHexString(binary);
                    }
                    //System.out.println(result.toString());
                    pre_result.setLength(0);
                    //drop hexadecimals values on end for new hexadecimal values.
                    for (int y = 0; y < DataBaseAddress.length() - result.length(); y++) {
                        pre_result.append(DataBaseAddress.charAt(y));
                    }
                    //System.out.println("Pre result: " + pre_result.toString());
                    //NOW we got to calculate new DataBaseAddress.
                    pre_result.append(result);
                    DataBaseAddress = pre_result.toString();
                    pre_result.setLength(0);
                    result.setLength(0);
                    lastStr += textLength;
                    textLength = 0;
                    storeWord.setLength(0); // clear string
                }
            }

        }
        // DO .text now.
        StringBuilder addressCache = new StringBuilder();
        File file1 = new File(args[0]);
        br = new BufferedReader(new FileReader(file1));
        while((st = br.readLine()) != null){
            //System.out.println("Text address: " + textBaseAddress);
            // Convert hex instruction to binary and process it.
            // WE WILL USE THIS IN THE WHILE LOOP WHEN WE READ .text files.
            String hexInstruction = st;
            //String binaryInstruction = hexToBinary(hexInstruction);
            //OPCODE WILL TELL US WHICH INSTRUCTION IT IS.
            InstructionInfo add1 = new InstructionInfo();
            Object test = new Object();
            add1.disassembleInstructions(st); // will convert machine code to binary.
            test = add1.determineType();
            textBaseAddress = changeAddress(textBaseAddress, file, "PC");

            //Regardless we will need to find registers, which we can find easily through converting the hexadecimal to binary and then to decimal.
            //Once you find register, you will need to use the register number to access the regValues in the corresponding register number array spot
            // Those values will be what you modify/ work with.
            // add, sub, and, or, slt
            if(test instanceof rType){//check if we are looking at a r-Type instruction.
                // We will have to be able to do artihmetic operations for all r-type instructions.(E.g. add, subtract)
                ((rType) test).setMachineCode(add1.machineCode); //transfer binary code to here.
                int rsIndex = Integer.parseInt(hexToBinary(((rType) test).rs),2); // convert to register indices in decimals(base 10)
                int rtIndex = Integer.parseInt(hexToBinary(((rType) test).rt),2);
                int rdIndex = Integer.parseInt(hexToBinary(((rType) test).rd),2);
                // We need to keep track of register values.
                // RegIndex 2 is $v0. RegIndex 4 is $a0.
                // Be able to perform instructions
                // IN ORDER TO DO MOVE, WE DO ADD.
                if(((rType) test).mnemonic.equals("add")){
                    regValues[rdIndex] = Integer.toString(Integer.parseInt(regValues[rsIndex]) + Integer.parseInt(regValues[rtIndex])); // rd = rs + rt
                }
                else if(((rType) test).mnemonic.equals("sub")){
                    regValues[rdIndex] = Integer.toString(Integer.parseInt(regValues[rsIndex]) - Integer.parseInt(regValues[rtIndex]));// rd = rs - rt
                }
                else if(((rType) test).mnemonic.equals("and")){
                    regValues[rdIndex] = Integer.toString(Integer.parseInt(regValues[rsIndex]) & Integer.parseInt(regValues[rtIndex]));
                }
                else if(((rType) test).mnemonic.equals("or")){
                    regValues[rdIndex] = Integer.toString(Integer.parseInt(regValues[rsIndex]) | Integer.parseInt(regValues[rtIndex]));
                }
                else if(((rType) test).mnemonic.equals("slt")){
                    int rsLess = 0;
                    if(Integer.parseInt(regValues[rsIndex]) < Integer.parseInt(regValues[rtIndex])){
                        rsLess = 1;
                    }
                    regValues[rdIndex] = Integer.toString(rsLess);
                }
                //addiu, lui, ori, andi, beq, bne. We need sw and lw.
            } else if(test instanceof iType){ //check if we are looking at a i-Type instruction.
                ((iType) test).setMachineCode(add1.machineCode); //transfer binary code to here.
                // li will be handled here. li will only handle single digits, so we should be fine.
                int immead = Integer.parseInt(hexToBinary(((iType) test).immediate),2);
                // store immeadiate value.
                int rsIndex = Integer.parseInt(hexToBinary(((iType) test).rs),2); // convert to register indices in decimals(base 10)
                int rtIndex = Integer.parseInt(hexToBinary(((iType) test).rt),2);
                if(((iType) test).mnemonic.equals("addiu")){ // assume rs is always 0.
                    regValues[rtIndex] = Integer.toString(Integer.parseInt(regValues[rsIndex]) + immead);
                }
                if(((iType) test).mnemonic.equals("lui")){ // assume rs is always 0.
                    addressCache.append(((iType) test).immediate); // load the upper 16 bit immeadiate value of rt.
                }
                if(((iType) test).mnemonic.equals("ori")){
                    addressCache.append(((iType) test).immediate); // load the upper 16 bit immeadiate value of rt.
                }
                if(((iType) test).mnemonic.equals("andi")){
                    regValues[rtIndex] = Integer.toString(Integer.parseInt(regValues[rsIndex]) & immead); // perform bit-wise and operation.
                }
                if(((iType) test).mnemonic.equals("beq")){
                    // the immeadiate(offset) will be used to locate next instruction.
                    // if the offset is 1, then the 2nd instruction relative to the current instruction is the one we need
                    // if the offset is 3, then the 4th instruction is what we need.
                    if(Integer.parseInt(regValues[rsIndex]) == Integer.parseInt(regValues[rtIndex])){
                        int offset = Integer.parseInt(hexToBinary(((iType) test).immediate), 2);
                        int i = 0;
                        while(i < offset){ // we don't have to wrroy about procedure declaration. it isn't detected.
                            br.readLine(); // skip next instruction.
                            textBaseAddress = changeAddress(textBaseAddress, file, "PC");
                            i++;
                        }
                    }
                }
                if(((iType) test).mnemonic.equals("bne")){
                    if(Integer.parseInt(regValues[rsIndex]) != Integer.parseInt(regValues[rtIndex])){
                        int offset = Integer.parseInt(hexToBinary(((iType) test).immediate), 2);
                        int i = 0;
                        while(i < offset){ // we don't have to wrroy about procedure declaration. it isn't detected.
                            br.readLine(); // skip next instruction.
                            textBaseAddress = changeAddress(textBaseAddress, file, "PC");
                            i++;
                        }
                    }
                }
                if(((iType) test).mnemonic.equals("sw")){ // just need to finish sw and lw!
                    int BaseAddress = Integer.parseInt(((iType) test).rs,10);
                    int offset = Integer.parseInt(((iType) test).immediate, 10);
                    String newAddress = Integer.toBinaryString(BaseAddress + offset);

                    dataAddresses[0][labelNums] = newAddress; // store address in memory
                    dataAddresses[1][labelNums] = regValues[Integer.parseInt(((iType) test).rt,10)]; // store value from rt in there.
                    labelNums++;
                }

                if(((iType) test).mnemonic.equals("lw")){
                    int BaseAddress = Integer.parseInt(((iType) test).rs,10);
                    int offset = Integer.parseInt(((iType) test).immediate, 10);
                    String newAddress = Integer.toBinaryString(BaseAddress + offset);

                    // search for address and retrieve the value.
                    for(int i = 0; i < dataAddresses[0].length; i++){
                        if(dataAddresses[0][i].equals(newAddress)){
                            System.out.println(10);
                            regValues[rtIndex] = dataAddresses[1][i]; // store value from dataAddresses into rt.
                            break;
                        }
                    }
                }
                //jump
            } else if (test instanceof jType){
                ((jType) test).setMachineCode(add1.machineCode); //transfer binary code to here.
                if(((jType) test).mnemonic.equals("j")){
                    StringBuilder result1 = new StringBuilder();
                    StringBuilder result2 = new StringBuilder();
                    //Shift to left.
                    String index = ((jType) test).index;
                    String targetAdd = hexToBinary(index);
                    targetAdd = targetAdd.substring(2,targetAdd.length()); // 26 bits as of right now.
                    // add extra 4 bits to make 32
                    result1.append("0000");
                    result1.append(targetAdd);
                    result1.append("00");// Shift left by 2.

                    long temp1 = 0;
                    for (int y = 0; y < result1.length(); y += 4) {
                        //calculate the hexadecimal value of the binary value of the numbers of characters
                        //we have seen so far.
                        String binary1;
                        binary1 = result1.substring(y, y + 4);
                        temp1 = Long.parseLong(binary1, 2);
                        result2.append(Long.toHexString(temp1));//Long.toHexString(binary);
                    }
                    result1.setLength(0);
                    //System.out.println(result2.toString());
                    String newAddress = result2.toString(); // the new text baseAddress to find.
                    result2.setLength(0);
                    // use newAddress to find the next instruction
                    while(!newAddress.equals(textBaseAddress)){
                        br.readLine();
                        textBaseAddress = changeAddress(textBaseAddress, file, "PC");
                    }
                }
            } else{ // syscall
                // RegIndex 2 is $v0. RegIndex 4 is $a0.
                // For syscall, we need to access the values in $v0. This determines what we are doing.
                // For this program, we will worry about three functionalities / numbers in $v0
                // print string & print integer
                if(regValues[2].equals("4") || regValues[2].equals("1") ){
                    // we will use address cache to access which string to print out to string.
                    // We need to access dataAddresses array.
                    // We need to first locate address in first row.
                    // Then locate value using that same index in the second row.
                    String address = addressCache.toString(); // We will check $a0 using addressCache for the value to be printed to screen if printing string.
                    String value = "";
                    for(int i = 0; i < dataAddresses[0].length; i++){
                        //System.out.println(dataAddresses[0][i]);
                        if(dataAddresses[0][i] == null) return; // null check.
                        if(dataAddresses[0][i].equals(address)){
                            value = dataAddresses[1][i]; // grab string to be printed.
                            break;
                        }
                    }
                    if(regValues[2].equals("1")){ // print an integer.
                        System.out.print(Integer.parseInt(value));
                        addressCache.setLength(0);
                    } else{ // print string
                        System.out.print(value);
                        addressCache.setLength(0);
                    }
                }
                // read integer
                 if(regValues[2].equals("5")){
                    Scanner scanner = new Scanner(System.in);
                    int integer = scanner.nextInt();
                    //value would be stored in $v0
                    regValues[2] = Integer.toString(integer);
                }
                // handle exit
                if(regValues[2].equals("10")){
                    System.out.print("\n-- program is finished running --\n");
                    return;
                }
            }
        }
        System.out.print("\n-- program finished running (dropped off bottom) --\n");
        }
    }
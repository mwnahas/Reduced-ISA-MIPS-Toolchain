import java.io.*;
import java.util.ArrayList;
public class Main {

    public static String chAddr(String currentAddress, String scenario){
        String preResult = "";
        StringBuilder result = new StringBuilder();
        if(scenario.equals("PC")){
            preResult = addHex(currentAddress,"00000004");
            result.append("0".repeat(Math.max(0, 8 - preResult.length())));
            result.append(preResult);
        }
        return result.toString();
    }

    public static void chBr(String currentAddress, File file, int currentLine, String[] arr, String label) throws IOException {
        BufferedReader bufferedReader= new BufferedReader(new FileReader(file));
        String line = "";
        int index = 0;
        int offset = 0;
        while(index != currentLine){
            line = bufferedReader.readLine();
            index++;
        }
        while((line = bufferedReader.readLine()) != null){
            if(index + 1 == currentLine + 1){
                currentAddress = chAddr(currentAddress, "PC");
                continue;
            }
            offset +=4;
            currentAddress = chAddr(currentAddress, "PC");
            if(line.contains(label)){
                break;
            }
        }
        offset = offset/4;
        arr[0] = Integer.toString(offset);

    }
    public static String chJ(String currentAddress, File file, int currentLine, String label) throws IOException {
        StringBuilder result = new StringBuilder();
        BufferedReader bufferedReader= new BufferedReader(new FileReader(file));
        String line = "";
        int index = 0;
        while(index != currentLine){
            index++;
        }
        int skip = 1;
        while((line = bufferedReader.readLine()) != null){
            int comment = line.trim().indexOf('#');
            if (comment == 0) continue;

            if(line.isBlank()){
                continue;
            }
            if(skip == 1){
                currentAddress = chAddr(currentAddress, "PC");
                skip--;
                continue;
            }
            currentAddress = chAddr(currentAddress, "PC");
            if(line.contains(label)){
                break;
            }
        }

        int decimal1 = Integer.parseInt(currentAddress, 16);
        int decimal2 = 4;

        int quotient = decimal1/decimal2;
        String binResult = Integer.toBinaryString(quotient);
        result.append("0".repeat(Math.max(0, 26 - binResult.length())));
        for(int i = 0; i < binResult.length(); i++){
            result.append(binResult.charAt(i));
        }

        return result.toString();
    }

    public static String addHex(String a, String b) {
        long intA = Long.parseLong(a, 16);
        long intB = Long.parseLong(b, 16);

        long sum = intA + intB;
        return Long.toHexString(sum);
    }

    public static String getF (String opCode, String[][]inst, String[][]func, String type){
        String field = "";
        for (int i = 0; i < 15; i++) {
            if (inst[0][i].equals(opCode)) {
                if (type.equals("opcode")) {
                    field = inst[1][i];
                    break;
                }
                if (type.equals("shamt")) {
                    field = func[0][i];
                    break;
                }
                if (type.equals("function")) {
                    field = func[1][i];
                    break;
                }
                break;
            }
        }
        return field;
    }

    public static void fReg (String reg, StringBuilder before){
        if (reg.length() < 5) {
            before.append("0".repeat(5 - reg.length()));
        }
    }
    public static int gReg (String regi, String[]reg){
        int result = 0;
        for (int i = 0; i < reg.length; i++) {
            if (regi.equals(reg[i])) {
                result = i;
                break;
            }
        }
        return result;
    }

    public static String decHex ( long binary)
    {
        return Long.toHexString(binary);
    }


    public static String decBin(int decimal)
    {

        StringBuilder binaryString = new StringBuilder();
        while (decimal != 0) {
            binaryString.insert(0, (decimal % 2));
            decimal /= 2;
        }
        while (binaryString.length() % 4 != 0) {
            binaryString.insert(0, "0");
        }
        return binaryString.toString();
    }
    public static String hexBin(String hexadecimal)
    {
        int i;
        char ch;
        StringBuilder binary = new StringBuilder();
        int returnedBinary;
        hexadecimal = hexadecimal.toUpperCase();
        for (i = 0; i < hexadecimal.length(); i++) {
            ch = hexadecimal.charAt(i);
            if (!Character.isDigit(ch)
                    && !((int) ch >= 65 && (int) ch <= 70)) {
                binary = new StringBuilder("Invalid Hex String");
                return binary.toString();
            }
            else if ((int)ch >= 65 && (int)ch <= 70)
                returnedBinary = (int)ch - 55;
            else
                returnedBinary
                        = Integer.parseInt(String.valueOf(ch));
            binary.append(decBin(returnedBinary));
        }
        return binary.toString();
    }

    public static void main(String[] args) throws IOException {
        StringBuilder before = new StringBuilder();
        StringBuilder result = new StringBuilder();

        String[][] labels = new String[50][50];

        int labelIndex = 0;
        int lastStr = 0;
        int lineNum = 0;

        String dataAddr = "10010000";
        String testAddr = "00400000";
        
        ArrayList<Character> fStr = new ArrayList<Character>();
        File file = new File(args[0]);
        int period =  args[0].indexOf('.');
        String fileName = args[0].substring(0, period);
        FileWriter data = new FileWriter(fileName + ".data");
        FileWriter textTest = new FileWriter(fileName + ".text");
        BufferedReader bufferedReader
                = new BufferedReader(new FileReader(file));
        String st;
        String[][] inst = {{"add", "addiu", "and", "andi", "beq", "bne", "j", "lui", "lw", "or", "ori",
                "slt", "sub", "sw", "syscall"},
                {"000000", "001001", "000000", "001100", "000100", "000101",
                        "000010", "001111", "100011", "000000", "001101", "000000",
                        "000000", "101011", "000000"}};
        String[][] func = {{"00000", "", "00000", "", "", "", "", "", "", "00000", "", "00000", "00000", "", ""},
                {"100000", "", "100100", "", "", "", "", "", "", "100101", "", "101010", "100010", "", "001100"}};
        String[] rs_sp = {"00000"};

        String[] reg = {"$zero", "$at", "$v0", "$v1", "$a0", "$a1", "$a2", "$a3", "$t0", "$t1", "$t2", "$t3", "$t4", "$t5", "$t6", "$t7",
                "$s0", "$s1", "$s2", "$s3", "$s4", "$s5", "$s6", "$s7", "$t8", "$t9", "$k0", "$k1", "$gp", "$sp", "$fp", "$ra"};

        boolean onData = false, onText = false;
        while ((st = bufferedReader.readLine()) != null) {
            lineNum++;
            int comment = st.trim().indexOf('#');
            if (comment == 0) continue;
            if(st.isBlank()){
                continue;
            }
            if(st.equals(".data")){
                onData = true;
                continue;
            }
            if (st.equals(".text")){
                onData = false;
                while(fStr.size() % 4 != 0){
                    fStr.add('\0');
                }
                for(int i = 3; i < fStr.size(); i += 4){
                    for(int j = i; j >= i - 3; j--) {
                        int num1 = (int)fStr.get(j);
                        String AsciiString = Integer.toBinaryString(num1);
                        int addZero = 8 - AsciiString.length();
                        while (addZero != 0){
                            before.append("0");
                            addZero--;
                        }
                        before.append(AsciiString);

                        long temp1;
                        for (int y = 0; y < 8; y += 4) {
                            String bin;
                            bin = before.substring(y, y + 4);
                            temp1 = Long.parseLong(bin, 2);
                            result.append(Long.toHexString(temp1));
                        }
                        data.append(result.toString());
                        before.setLength(0);
                        result.setLength(0);
                    }

                    data.append("\n");
                }

                data.close();
                onText = true;
                continue;
            }
            if(onData){
                int num2 = st.indexOf("\"");
                int num3 = st.indexOf("\"",num2 + 1);
                String label = st.substring(0,st.indexOf(":")).trim();
                String text = st.substring(num2 + 1, num3);
                text += '\0';
                for(int i = 0; i < text.length(); i++){
                    fStr.add(text.charAt(i));
                }
                labels[0][labelIndex] = label;
                labels[1][labelIndex] = dataAddr;
                int length = text.length() + 1 + lastStr;
                String addBinary = Integer.toBinaryString(length);
                long temp1 = 0;
                before.append("0".repeat(Math.max(0, 4 - (addBinary.length() - 4))));
                before.append(addBinary);
                for (int i = 0; i < before.length(); i += 4) {
                    String binary;
                    binary = before.substring(i, i + 4);
                    temp1 = Long.parseLong(binary, 2);
                    result.append(decHex(temp1));
                }
                before.setLength(0);
                for(int i = 0; i < dataAddr.length() - result.length(); i++){
                    before.append(dataAddr.charAt(i));
                }
                before.append(result);
                dataAddr = before.toString();
                before.setLength(0);
                result.setLength(0);
                labelIndex++;
                lastStr += text.length() + 1;


                continue;
            }

            if(onText){
                String opCode = "";
                int sp = 0;
                for (int i = 0; i < st.length(); i++) {
                    char c = st.charAt(i);
                    if (!Character.isWhitespace(c)) {
                        sp = st.indexOf(" ", i);
                        if (sp == -1) {
                            opCode = st.substring(i);
                        } else {
                            opCode = st.substring(i, sp);
                        }
                        break;
                    }
                }
                if(opCode.equals("#")){
                    continue;
                }
                int comment_index = st.indexOf("#");
                String opcode = "";
                String rs = "";
                String rt = "";
                String rd = "";
                String shamt = "";
                String function = "";


                int rs_index = 0;
                int rt_index = 0;
                int rd_index = 0;

                opcode = getF(opCode, inst, func, "opcode");
                shamt = getF(opCode, inst, func, "shamt");
                function = getF(opCode, inst, func, "function");
                int first_comma_posit = st.indexOf(",");
                int second_comma_posit = 0;
                if (first_comma_posit != -1) {
                    second_comma_posit = st.indexOf(",", first_comma_posit + 1);
                }

                int register1 = 0;
                int register2 = 0;
                int register3 = 0;
                String immediate = "";
                int third_input = 0;

                if (opCode.equals("add") || opCode.equals("and") || opCode.equals("or") || opCode.equals("slt") || opCode.equals("sub")) {
                    testAddr = chAddr(testAddr, "PC");

                    register1 = st.indexOf("$");
                    rd = st.substring(register1, first_comma_posit).trim();

                    register2 = st.indexOf("$", register1 + 1);
                    rs = st.substring(register2, second_comma_posit).trim();

                    register3 = st.indexOf("$", register2 + 1);
                    rt = st.substring(register3).trim();
                    for (int i = 0; i < reg.length; i++) {
                        if (rd.equals(reg[i])) {
                            rd_index = i;
                        }
                        if (rs.equals(reg[i])) {
                            rs_index = i;
                        }
                        if (rt.equals(reg[i])) {
                            rt_index = i;
                        }
                    }

                    rd = Integer.toBinaryString(rd_index);
                    rs = Integer.toBinaryString(rs_index);
                    rt = Integer.toBinaryString(rt_index);
                    before.append(opcode);
                    fReg(rs, before);

                    before.append(rs);
                    fReg(rt, before);
                    before.append(rt);
                    fReg(rd, before);

                    before.append(rd);
                    before.append(shamt);
                    before.append(function);
                    long temp = 0;
                    for (int i = 0; i < 32; i += 4) {
                        String binary;
                        binary = before.substring(i, i + 4);
                        temp = Long.parseLong(binary, 2);
                        result.append(decHex(temp));
                    }
                    textTest.append(result.toString());
                    textTest.append("\n");
                    before.setLength(0);
                    result.setLength(0);
                } else if (opCode.equals("addiu") || opCode.equals("beq") || opCode.equals("bne") || opCode.equals("andi") || opCode.equals("ori")) {

                    register1 = st.indexOf("$");
                    rs = st.substring(register1, first_comma_posit);

                    register2 = st.indexOf("$", register1 + 1);
                    rt = st.substring(register2, second_comma_posit);

                    third_input = st.indexOf("0x");

                    if (third_input == -1) {
                        if (comment_index == -1) {
                            immediate = st.substring(second_comma_posit + 2).trim();
                        } else {
                            immediate = st.substring(second_comma_posit + 2, comment_index).trim();
                        }
                    } else {
                        if (comment_index == -1) {
                            immediate = st.substring(third_input + 2).trim();
                        } else {
                            immediate = st.substring(third_input + 2, comment_index).trim();
                        }

                    }

                    rs_index = gReg(rs, reg);
                    rt_index = gReg(rt, reg);
                    rs = Integer.toBinaryString(rs_index);
                    rt = Integer.toBinaryString(rt_index);
                    before.append(opcode);
                    if (opCode.equals("beq") || opCode.equals("bne")) {
                        testAddr = chAddr(testAddr, "PC");
                        String[] arr = new String[2];
                        chBr(testAddr, file, lineNum, arr, immediate);
                        immediate = arr[0];

                        fReg(rs, before);
                        before.append(rs);
                        fReg(rt, before);
                        before.append(rt);
                    } else {
                        fReg(rt, before);
                        before.append(rt);
                        fReg(rs, before);
                        before.append(rs);
                        testAddr = chAddr(testAddr, "PC");
                    }
                    if (third_input == -1) {
                        immediate = Integer.toBinaryString(Integer.parseInt(immediate));
                        if (immediate.length() > 16) {
                            immediate = immediate.substring(16, 32);
                        }
                        before.append("0".repeat(16 - immediate.length()));
                        before.append(immediate);
                    } else {
                        before.append("0000".repeat(Math.max(0, 4 - immediate.length())));
                    }

                    long temp = 0;
                    for (int i = 0; i < before.length(); i += 4) {
                        String binary;
                        if (i + 4 > before.length()) {
                            binary = before.substring(i, before.length());
                        } else {
                            binary = before.substring(i, i + 4);
                        }
                        temp = Long.parseLong(binary, 2);
                        result.append(decHex(temp));
                    }
                    if (third_input != -1) {
                        result.append(immediate);
                    }

                    textTest.append(result.toString());
                    textTest.append("\n");

                    result.setLength(0);
                    before.setLength(0);
                } else if (opCode.equals("j")) {
                    testAddr = chAddr(testAddr, "PC");
                    int first_space = st.indexOf(" ");
                    if (comment_index == -1) {
                        immediate = st.substring(first_space + 1).trim();
                    } else {
                        immediate = st.substring(first_space + 1, comment_index).trim();
                    }
                    immediate = chJ(testAddr,file, lineNum, immediate);
                    before.append(opcode);
                    before.append(immediate);

                    long temp = 0;
                    for (int i = 0; i < before.length(); i += 4) {
                        String binary;
                        binary = before.substring(i, i + 4);
                        temp = Long.parseLong(binary, 2);
                        result.append(decHex(temp));
                    }

                    textTest.append(result.toString());
                    textTest.append("\n");

                    result.setLength(0);
                    before.setLength(0);

                } else if (opCode.equals("lui")) {
                    testAddr = chAddr(testAddr, "PC");
                    rs = rs_sp[0];
                    register1 = st.indexOf("$");
                    rt = st.substring(register1, first_comma_posit).trim();
                    rt_index = gReg(rt, reg);
                    rt = Integer.toBinaryString(rt_index);

                    before.append(opcode);
                    int immead_index = st.indexOf("0x");
                    if (comment_index == -1) {
                        immediate = st.substring(immead_index + 2).trim();
                    } else {
                        immediate = st.substring(immead_index + 2, comment_index).trim();
                    }
                    before.append(rs);
                    fReg(rt, before);
                    before.append(rt);
                    before.append("0000".repeat(Math.max(0, 4 - immediate.length())));

                    long temp = 0;
                    for (int i = 0; i < before.length(); i += 4) {
                        String binary;
                        if (i + 4 > before.length()) {
                            binary = before.substring(i, before.length());
                        } else {
                            binary = before.substring(i, i + 4);
                        }
                        temp = Long.parseLong(binary, 2);
                        result.append(decHex(temp));
                    }
                    result.append(immediate);
                    textTest.append(result.toString());
                    textTest.append("\n");
                    result.setLength(0);
                    before.setLength(0);
                } else if (opCode.equals("lw") || opCode.equals("sw")) {
                    testAddr = chAddr(testAddr, "PC");
                    int parent1 = st.indexOf("(");
                    int parent2 = st.indexOf(")");
                    register1 = st.indexOf("$");
                    rt = st.substring(register1, first_comma_posit).trim();

                    rt_index = gReg(rt, reg);
                    rs = st.substring(parent1 + 1, parent2);
                    rs_index = gReg(rs, reg);
                    rt = Integer.toBinaryString(rt_index);
                    rs = Integer.toBinaryString(rs_index);
                    before.append(opcode);
                    fReg(rs, before);
                    before.append(rs);
                    fReg(rt, before);
                    before.append(rt);

                    String offset = st.substring(first_comma_posit + 1, parent1).trim();

                    if (!offset.isBlank()) {
                        offset = Integer.toBinaryString(Integer.parseInt(offset));
                        if (offset.length() < 16) {
                            before.append("0".repeat(16 - offset.length()));
                        } else {

                            offset = offset.substring(16, 32);
                        }
                    } else {
                        offset = "0000000000000000";
                    }
                    before.append(offset);
                    long temp = 0;
                    for (int i = 0; i < before.length(); i += 4) {
                        String binary;
                        if (i + 4 > before.length()) {
                            binary = before.substring(i, before.length());
                        } else {
                            binary = before.substring(i, i + 4);
                        }
                        temp = Long.parseLong(binary, 2);
                        result.append(decHex(temp));
                    }
                    textTest.append(result.toString());
                    textTest.append("\n");
                    result.setLength(0);
                    before.setLength(0);

                } else if (opCode.equals("syscall")) {
                    testAddr = chAddr(testAddr, "PC");

                    textTest.append("0000000c\n");
                } else if (opCode.equals("move")) {

                    testAddr = chAddr(testAddr, "PC");
                    opcode = getF("add", inst, func, "opcode");
                    shamt = getF("add", inst, func, "shamt");
                    function = getF("add", inst, func, "function");

                    register1 = st.indexOf("$");
                    rd = st.substring(register1, first_comma_posit);


                    register2 = st.indexOf("$", register1 + 1);

                    if (comment_index == -1) {
                        rs = st.substring(register2).trim();
                    } else {
                        rs = st.substring(register2, comment_index);
                    }

                    rt = "$zero";

                    rd_index = gReg(rd, reg);
                    rs_index = gReg(rs, reg);
                    rt_index = gReg(rt, reg);

                    rd = Integer.toBinaryString(rd_index);
                    rs = Integer.toBinaryString(rs_index);
                    rt = Integer.toBinaryString(rt_index);
                    before.append(opcode);
                    fReg(rs, before);

                    before.append(rs);
                    fReg(rt, before);
                    before.append(rt);
                    fReg(rd, before);

                    before.append(rd);
                    before.append(shamt);
                    before.append(function);
                    long temp = 0;
                    for (int i = 0; i < 32; i += 4) {
                        String binary;
                        binary = before.substring(i, i + 4);
                        temp = Long.parseLong(binary, 2);
                        result.append(decHex(temp));
                    }
                    textTest.append(result.toString());
                    textTest.append("\n");
                    result.setLength(0);
                    before.setLength(0);
                } else if (opCode.equals("li")) {
                    testAddr = chAddr(testAddr, "PC");

                    register1 = st.indexOf("$");
                    rt = st.substring(register1, first_comma_posit).trim();
                    rt_index = gReg(rt, reg);

                    if (comment_index == -1) {
                        immediate = st.substring(first_comma_posit + 1).trim();
                    } else {
                        immediate = st.substring(first_comma_posit + 1, comment_index).trim();
                    }
                    long tempim= Long.parseLong(immediate,10);
                    rt = Integer.toBinaryString(rt_index);

                    if (tempim>= -32768 && tempim<= 65535) {

                        before.append("001101");
                        before.append("00000");
                        fReg(rt, before);
                        before.append(rt);
                        before.append(String.format("%16s", Long.toBinaryString(tempim& 0xFFFF)).replace(' ', '0'));
                    } else {

                        int u = (int) ((tempim>> 16) & 0xFFFF);
                        int l = (int) (tempim& 0xFFFF);


                        before.append("001111");
                        before.append("00000");
                        before.append(Integer.toBinaryString(rt_index));
                        before.append(String.format("%16s", Integer.toBinaryString(u)).replace(' ', '0'));

                        long temp = 0;
                        for (int i = 0; i < 32; i += 4) {
                            String binary;
                            binary = before.substring(i, i + 4);
                            temp = Long.parseLong(binary, 2);
                            result.append(decHex(temp));
                        }
                        textTest.append(result.toString());

                        result.setLength(0);


                        result.append("001101");
                        result.append(Integer.toBinaryString(rt_index));
                        result.append(Integer.toBinaryString(rt_index));
                        result.append(String.format("%16s", Integer.toBinaryString(l)).replace(' ', '0'));
                        textTest.append(result.toString());
                        result.setLength(0);
                        before.setLength(0);
                        continue;
                    }
                    long temp = 0;
                    for (int i = 0; i < 32; i += 4) {
                        String binary;
                        binary = before.substring(i, i + 4);
                        temp = Long.parseLong(binary, 2);
                        result.append(decHex(temp));
                    }
                    textTest.append(result.toString());
                    textTest.append("\n");
                    result.setLength(0);
                    before.setLength(0);
                } else if (opCode.equals("la")){
                    testAddr = chAddr(testAddr, "PC");

                    String address01 = "";
                    register1 = st.indexOf("$");
                    rt = st.substring(register1, first_comma_posit).trim();
                    rt_index = gReg(rt, reg);
                    rt = Integer.toBinaryString(rt_index);

                    String addressLabel = st.substring(first_comma_posit + 1).trim();
                    for(int i = 0; i < labels[0].length; i++){
                        if(addressLabel.equals(labels[0][i])){
                            address01 = labels[1][i];
                            break;
                        }
                    }
                    address01 = hexBin(address01);
                    before.append("0".repeat(Math.max(0, 32 - address01.length())));
                    before.append(address01);
                    String uStr = before.substring(0,16);
                    String lStr = before.substring(16,32);

                    before.append("001111");
                    before.append("00000");
                    fReg(rt, before);
                    before.append(rt);
                    before.append(uStr);

                    long temp = 0;
                    for (int i = 0; i < 32; i += 4) {
                        String binary;
                        binary = before.substring(i, i + 4);
                        temp = Long.parseLong(binary, 2);
                        result.append(decHex(temp));
                    }
                    textTest.append(result.toString());
                    textTest.append("\n");

                    result.setLength(0);
                    before.setLength(0);

                    before.append("001101");
                    fReg(rt,before);
                    before.append(rt);
                    fReg(rt,before);
                    before.append(rt);

                    before.append(lStr);
                    for (int i = 0; i < 32; i += 4) {
                        String binary;
                        binary = before.substring(i, i + 4);
                        temp = Long.parseLong(binary, 2);
                        result.append(decHex(temp));
                    }

                    textTest.append(result.toString());
                    textTest.append("\n");
                    result.setLength(0);
                    before.setLength(0);
                } else if(opCode.equals("blt")) {
                    testAddr = chAddr(testAddr, "PC");
                    register1 = st.indexOf("$");
                    register2 = st.indexOf("$", register1 + 1);
                    rs = st.substring(register1, first_comma_posit).trim();
                    rt = st.substring(register2, second_comma_posit).trim();

                    rs_index = gReg(rs, reg);
                    rt_index = gReg(rt, reg);
                    rs = Integer.toBinaryString(rs_index);
                    rt = Integer.toBinaryString(rt_index);
                    String at = Integer.toBinaryString(gReg("$at", reg));

                    before.append("000000");
                    fReg(rs,before);
                    before.append(rs);

                    fReg(rt,before);
                    before.append(rt);
                    fReg(at, before);
                    before.append(at);
                    before.append("00000");
                    before.append("101010");

                    long temp = 0;
                    for (int i = 0; i < 32; i += 4) {
                        String binary;
                        binary = before.substring(i, i + 4);
                        temp = Long.parseLong(binary, 2);
                        result.append(decHex(temp));
                    }
                    textTest.append(result.toString());
                    textTest.append("\n");
                    result.setLength(0);
                    before.setLength(0);

                    before.append("000101");
                    fReg(at, before);
                    before.append(at);
                    before.append("00000");
                    before.append(String.format("%16s", Integer.toBinaryString(4 & 0xFFFF)).replace(' ', '0'));

                    for (int i = 0; i < 32; i += 4) {
                        String binary;
                        binary = before.substring(i, i + 4);
                        temp = Long.parseLong(binary, 2);
                        result.append(decHex(temp));
                    }
                    textTest.append(result.toString());
                    textTest.append("\n");
                    result.setLength(0);
                    before.setLength(0);
                } else{
                    testAddr = chAddr(testAddr, "PC");
                }
            }
        }
        textTest.close();
    }

}
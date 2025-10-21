/*
 * This performs the translation of r-Type MIPS instructions in reduced Instruction Set MIPS simulator.
 */
public class rType extends InstructionInfo{
    String machineCode;
    String mnemonic;
    String opcode;
    String rs;
    String rt;
    String rd;
    String shmt;
    String funct;

    // Declaring Variables for translating to hexadecimal
    int ans;
    String temp;

    public rType(){
        super();
    }

    public void setMachineCode(String machineCode){
        this.machineCode = machineCode;
        getOpcode();
        getRegisters();
        getShamt();
        getFunct();
        getMnemonic();
    }

    public void getOpcode(){
        this.opcode = machineCode.substring(0,6);
        //Binary to decimal Conversion
        temp = this.opcode;
        ans = Integer.parseInt(temp, 2);
        //Convert decimal to hex.
        opcode = Integer.toHexString(ans);
        if(opcode.length() == 1) {
            opcode = "0" + opcode;
        }
        //System.out.println("Opcode: " + opcode);
    }


    //RULE: Hexadecimal at mimimum should be length of 2.
    private void getRegisters(){
        this.rs = machineCode.substring(6,11);
        //Binary to decimal Conversion
        temp = this.rs;
        ans = Integer.parseInt(temp, 2);
        //Convert decimal to hex.
        rs = Integer.toHexString(ans);
        if(rs.length() == 1) {
            rs = "0" + rs;
        }

        this.rt = machineCode.substring(11,16);
        //Binary to decimal Conversion
        temp = this.rt;
        ans = Integer.parseInt(temp, 2);
        //rs = Integer.toString(ans);
        //Convert decimal to hex.
        rt = Integer.toHexString(ans);
        if(rt.length() == 1) {
            rt = "0" + rt;
        }

        this.rd = machineCode.substring(16,21);
        //Binary to decimal Conversion
        temp = this.rd;
        ans = Integer.parseInt(temp, 2);
        //Convert decimal to hex.
        rd = Integer.toHexString(ans);
        if(rd.length() == 1) {
            rd = "0" + rd;
        }

        //System.out.println("RS: " + rs);
        //System.out.println("RT: " + rt);
        //System.out.println("RD: " + rd);
    }

    private void getShamt(){
        this.shmt = machineCode.substring(21,26);
        //Binary to decimal Conversion
        temp = this.shmt;
        ans = Integer.parseInt(temp, 2);
        //Convert decimal to hex.
        shmt = Integer.toHexString(ans);
        if(shmt.length() == 1) {
            shmt = "0" + shmt;
        }
        //System.out.println("shmt: " + shmt);
    }

    private void getFunct(){
        this.funct = machineCode.substring(26,32);
        //Binary to decimal Conversion
        temp = this.funct;
        ans = Integer.parseInt(temp, 2);
        //rs = Integer.toString(ans);
        //Convert decimal to hex.
        funct = Integer.toHexString(ans);
        if(funct.length() == 1) {
            funct = "0" + funct;
        }
        //System.out.println("funct: " + funct);
    }

    //We will use funct to determine mnemonics
    private void getMnemonic(){
        if (this.funct.equals("20")){ // set mnemonic to add
            this.mnemonic = "add";
        }
        if(this.funct.equals("22")){ //set mnemonic to sub
            this.mnemonic = "sub";
        }
        if (this.funct.equals("24")){ // set mnemonic to and
            this.mnemonic = "and";
        }
        if(this.funct.equals("25")){// set mnemonic to or
            this.mnemonic = "or";
        }
        if(this.funct.equals("2a")){//set mnemonic to 2a
            this.mnemonic = "slt";
        }
    }

    public String toString(){
        return this.mnemonic + " {opcode: " + opcode + ", rs: " + rs + ", rt: " + rt + ", rd: " + rd + ", shmt: " + shmt +", funct: "+ funct + "}";
        //Machine code in r type: " + this.machineCode;
    }

}

public class iType extends InstructionInfo{
    String machineCode;
    String mnemonic;
    String opcode;
    String rs;
    String rt;
    String immediate;

    // Declaring Variables for translating to hexadecimal
    int ans;
    String temp;

    public iType(){
        super();
    }

    public void setMachineCode(String machineCode){
        this.machineCode = machineCode;
        getOpcode();
        getRegisters();
        getImmediate();
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

        //System.out.println("RS: " + rs);
        //System.out.println("RT: " + rt);
    }

    private void getImmediate(){
        this.immediate = machineCode.substring(16,32);
        //Binary to decimal Conversion
        temp = this.immediate;
        ans = Integer.parseInt(temp, 2);
        //Convert decimal to hex.
        immediate = Integer.toHexString(ans);
        for(int i = 0; i <= 4 - immediate.length(); i++){
            if(immediate.length() == 4) break;
            this.immediate = "0" + this.immediate;
        }

        if(immediate.length() != 4){ //extra check to make sure immeadiate is really 4 digits.
            this.immediate = "0" + this.immediate;
        }
        //System.out.println("immediate: " + immediate);
    }

    // we use the opcode to find the specific I-Type instructions.
    // All we have left is sw  to implement here.
    private void getMnemonic(){
        if(this.opcode.equals("09")){// set mnemonic to addiu
            this.mnemonic = "addiu";
        }
        if(this.opcode.equals("0c")){// set mnemonic to andi
            this.mnemonic = "andi";
        }
        if(this.opcode.equals("04")){// set mnemonic to beq
            this.mnemonic = "beq";
        }
        if(this.opcode.equals("05")){// set mnemonic to bne
            this.mnemonic = "bne";
        }
        if(this.opcode.equals("0f")){// set mnemonic to lui
            this.mnemonic = "lui";
        }
        if(this.opcode.equals("23")){// set mnemonic to lw
            this.mnemonic = "lw";
        }
        if(this.opcode.equals("2b")){// set mnemonic to sw
            this.mnemonic = "sw";
        }
        if(this.opcode.equals("0d")){// set mnemonic to ori
            this.mnemonic = "ori";
        }
    }

    public String toString(){
        return this.mnemonic + " {opcode: " + opcode + ", rs(base): " + rs + ", rt: " + rt + ", immediate(offset): "+ immediate + "}";
    }
}

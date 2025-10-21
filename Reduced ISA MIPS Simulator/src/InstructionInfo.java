public class InstructionInfo {
    String machineCode;
    String opcode;

    public InstructionInfo(){

    }

    //return machine code as binary.
    public void disassembleInstructions(String code){
        if(code.equals("0000000c")) {
            this.machineCode = code;
            return; // no modification if syscall.
        }
        // Convert the hex string to a binary string, ensuring leading zeros are preserved
        long decimal = Long.parseLong(code, 16); // Parse hex as base 16 (long for large values)
        String binary = Long.toBinaryString(decimal); // Convert to binary string

        // Ensure the binary string is 32 bits (for 8 hex characters)
        binary = String.format("%32s", binary).replace(' ', '0');

        this.machineCode = binary;
        //System.out.println(machineCode);
        getOpCode(); // We are going to use the opcode to determine what we will have to do.
        /*getRegisters();
        getShamt();
        getFunct();*/
    }

    //opcode will help us figure out which type of instruction.
    // 000000 is R-TYPE
    private void getOpCode(){
        assert machineCode != null;
        opcode = machineCode.substring(0,6);
    }

    //we will determine instruction type using opcode with the exception of syscall.
    public Object determineType() {
        if(machineCode.equals("0000000c")) return new syscall();
        switch(opcode){
            case "000000":
                return new rType();
            case "000010":
                return new jType();
        }
        return new iType();// immeadiate type detected
    }

}

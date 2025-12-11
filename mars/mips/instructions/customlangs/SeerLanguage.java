package mars.mips.instructions.customlangs;

import mars.simulator.*;
import mars.mips.hardware.*;
import mars.*;
import mars.util.*;
import mars.mips.instructions.*;

/**
 * SEER custom assembly language for MARS LE.
 *
 * Implements these instructions
 *  - bless, curse, tarot, foresee, fate, align, focus
 *  - ifgood, ifbad, whisper
 *  - cast, yesno, choose, blessall, drain
 *  - crystal, seerface, clearveil, glowrow, glowcol
 *
 * NOTE: Bitmap helper instructions assume the standard MARS Bitmap
 * Display is memory-mapped starting at 0x10008000 and laid out row-major.
 */
public class SeerLanguage extends CustomAssembly {

   
    private static final int BITMAP_BASE  = 0x10008000;
    private static final int BITMAP_WIDTH = 512;  
    private static final int BITMAP_HEIGHT = 512;  

    @Override
    public String getName() {
        return "SEER Language";
    }

    @Override
    public String getDescription() {
        return "A fortune-teller themed extension language for MARS LE.";
    }

    @Override
    protected void populate() {

        

       
        instructionList.add(
            new BasicInstruction(
                "bless $t1,-100",
                "Increase the main fortune register by an immediate value",
                BasicInstructionFormat.I_FORMAT,
                "001001 fffff 00000 ssssssssssssssss",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement)
                            throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int fate = RegisterFile.getValue(operands[0]);
                        int imm  = operands[1] << 16 >> 16;  
                        int result = fate + imm;
                        RegisterFile.updateRegister(operands[0], result);
                    }
                }
            )
        );

     
        instructionList.add(
            new BasicInstruction(
                "curse $t1,-100",
                "Decrease the main fortune register by an immediate value",
                BasicInstructionFormat.I_FORMAT,
                "001010 fffff 00000 ssssssssssssssss",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement)
                            throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int fate = RegisterFile.getValue(operands[0]);
                        int imm  = operands[1] << 16 >> 16;
                        int result = fate - imm;
                        RegisterFile.updateRegister(operands[0], result);
                    }
                }
            )
        );

       
        instructionList.add(
            new BasicInstruction(
                "tarot $t1,$t2",
                "Set fate to the stronger of fate and aura",
                BasicInstructionFormat.R_FORMAT,
                "000000 fffff sssss 00000 00000 101010",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement)
                            throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int fate = RegisterFile.getValue(operands[0]);
                        int aura = RegisterFile.getValue(operands[1]);
                        int result = (Math.abs(aura) > Math.abs(fate)) ? aura : fate;
                        RegisterFile.updateRegister(operands[0], result);
                    }
                }
            )
        );

       
        instructionList.add(
            new BasicInstruction(
                "foresee $t1,$t2",
                "Average vision with fate and store in vision",
                BasicInstructionFormat.R_FORMAT,
                "000000 fffff sssss 00000 00000 101011",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement)
                            throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int vision = RegisterFile.getValue(operands[0]);
                        int fate   = RegisterFile.getValue(operands[1]);
                        int avg = (vision + fate) / 2;
                        RegisterFile.updateRegister(operands[0], avg);
                    }
                }
            )
        );

      
        instructionList.add(
            new BasicInstruction(
                "fate $t1,$t2",
                "XOR omens with karma",
                BasicInstructionFormat.R_FORMAT,
                "000000 fffff sssss 00000 00000 101100",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement)
                            throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int omens = RegisterFile.getValue(operands[0]);
                        int karma = RegisterFile.getValue(operands[1]);
                        RegisterFile.updateRegister(operands[0], omens ^ karma);
                    }
                }
            )
        );

       
        instructionList.add(
            new BasicInstruction(
                "align $t1,$t2",
                "Align fate relative to destiny as -1, 0, or 1",
                BasicInstructionFormat.R_FORMAT,
                "000000 fffff sssss 00000 00000 101101",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement)
                            throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int fate    = RegisterFile.getValue(operands[0]);
                        int destiny = RegisterFile.getValue(operands[1]);
                        int result;
                        if (fate < destiny)      result = -1;
                        else if (fate > destiny) result = 1;
                        else                     result = 0;
                        RegisterFile.updateRegister(operands[0], result);
                    }
                }
            )
        );

     
        instructionList.add(
            new BasicInstruction(
                "focus $t1",
                "Clamp aura into range [-100, 100]",
                BasicInstructionFormat.R_FORMAT,
                "000000 fffff 00000 00000 00000 101110",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement)
                            throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int aura = RegisterFile.getValue(operands[0]);
                        if (aura > 100) aura = 100;
                        if (aura < -100) aura = -100;
                        RegisterFile.updateRegister(operands[0], aura);
                    }
                }
            )
        );

     
   
        instructionList.add(
            new BasicInstruction(
                "ifgood $t1,label",
                "Branch to label if fate is positive",
                BasicInstructionFormat.I_BRANCH_FORMAT,
                "000101 fffff 00000 ssssssssssssssss",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement)
                            throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int fate = RegisterFile.getValue(operands[0]);
                        if (fate > 0) {
                           
                            Globals.instructionSet.processBranch(operands[1]);
                        }
                    }
                }
            )
        );

        
        instructionList.add(
            new BasicInstruction(
                "ifbad $t1,label",
                "Branch to label if fate is negative",
                BasicInstructionFormat.I_BRANCH_FORMAT,
                "000110 fffff 00000 ssssssssssssssss",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement)
                            throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int fate = RegisterFile.getValue(operands[0]);
                        if (fate < 0) {
                            Globals.instructionSet.processBranch(operands[1]);
                        }
                    }
                }
            )
        );

      
        instructionList.add(
            new BasicInstruction(
                "whisper label",
                "Print zero-terminated string stored at label",
                BasicInstructionFormat.I_BRANCH_FORMAT,
                "110001 00000 00000 ffffffffffffffff",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement)
                            throws ProcessingException {

                     
                        String label =
                            statement.getOriginalTokenList().get(1).getValue();

                        int byteAddress =
                            Globals.program.getLocalSymbolTable()
                                    .getAddressLocalOrGlobal(label);

                        try {
                            char ch = (char) Globals.memory.getByte(byteAddress);
                            while (ch != 0) {
                                SystemIO.printString(Character.toString(ch));
                                byteAddress++;
                                ch = (char) Globals.memory.getByte(byteAddress);
                            }
                        } catch (AddressErrorException e) {
                            throw new ProcessingException(statement, e);
                        }
                    }
                }
            )
        );

     

        instructionList.add(
            new BasicInstruction(
                "cast $t1",
                "Normalize fate into range [-50, 50]",
                BasicInstructionFormat.R_FORMAT,
                "000000 fffff 00000 00000 00000 110000",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement)
                            throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int fate = RegisterFile.getValue(operands[0]);
                        int mod = ((fate % 101) + 101) % 101;
                        int result = mod - 50;
                        RegisterFile.updateRegister(operands[0], result);
                    }
                }
            )
        );

    
        instructionList.add(
            new BasicInstruction(
                "yesno $t1",
                "Collapse fate into 0 (no) or 1 (yes)",
                BasicInstructionFormat.R_FORMAT,
                "000000 fffff 00000 00000 00000 110001",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement)
                            throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int fate = RegisterFile.getValue(operands[0]);
                        int result = (fate > 0) ? 1 : 0;
                        RegisterFile.updateRegister(operands[0], result);
                    }
                }
            )
        );

       
        instructionList.add(
            new BasicInstruction(
                "choose $t1,$t2",
                "Map fate into an index in [0, destiny-1]",
                BasicInstructionFormat.R_FORMAT,
                "000000 fffff sssss 00000 00000 110010",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement)
                            throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int fate    = RegisterFile.getValue(operands[0]);
                        int destiny = RegisterFile.getValue(operands[1]);
                        int result = 0;
                        if (destiny > 0) {
                            int mod = ((fate % destiny) + destiny) % destiny;
                            result = mod;
                        }
                        RegisterFile.updateRegister(operands[0], result);
                    }
                }
            )
        );

      
        instructionList.add(
            new BasicInstruction(
                "blessall $t1",
                "Sum all fate channels (t0..t7) into spirits",
                BasicInstructionFormat.R_FORMAT,
                "000000 fffff 00000 00000 00000 110011",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement)
                            throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int sum = 0;
                     
                        for (int reg = 8; reg <= 15; reg++) {
                            sum += RegisterFile.getValue(reg);
                        }
                        RegisterFile.updateRegister(operands[0], sum);
                    }
                }
            )
        );

        // uplift: mystical LUI (3 operands: rd, rs, imm16; rs is ignored)
        instructionList.add(
                new BasicInstruction(
                        "uplift $t1, $t2, 100",                     // ✅ 3 operands: rd, rs, imm
                        "Load upper 16 bits of an immediate into $t1 (like lui; $t2 is ignored)",
                        BasicInstructionFormat.I_FORMAT,
                        // opcode=001111, rs=sssss, rt=fffff (dest), imm=tttt...
                        "001111 sssss fffff tttttttttttttttt",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement)
                                    throws ProcessingException {

                                int[] operands = statement.getOperands();
                                int imm = operands[2] & 0xFFFF;     // third operand = imm16
                                int value = imm << 16;              // move into upper half

                                // first operand is dest register
                                RegisterFile.updateRegister(operands[0], value);
                            }
                        }
                )
        );


        instructionList.add(
                new BasicInstruction(
                        "weave $t1, $t2, 100",
                        "OR immediate: $t1 = $t2 | (imm & 0xFFFF) (like ori)",
                        BasicInstructionFormat.I_FORMAT,
                        // opcode = 001101 (ori), rs = sssss (source), rt = fffff (dest), imm = tttt...
                        "001101 sssss fffff tttttttttttttttt",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement)
                                    throws ProcessingException {

                                int[] operands = statement.getOperands();

                                int rsVal = RegisterFile.getValue(operands[1]);
                                int imm   = operands[2] & 0xFFFF;   // 16-bit immediate, zero-extended

                                int result = rsVal | imm;
                                RegisterFile.updateRegister(operands[0], result);
                            }
                        }
                )
        );



        instructionList.add(
                new BasicInstruction(
                        "j label",
                        "Jump to label",
                        BasicInstructionFormat.I_BRANCH_FORMAT,

                        "000010 00000 00000 ffffffffffffffff",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement)
                                    throws ProcessingException {

                                int[] operands = statement.getOperands();

                                Globals.instructionSet.processBranch(operands[0]);
                            }
                        }
                )
        );



        instructionList.add(
                new BasicInstruction(
                        "beq $t1,$t2,label",
                        "Branch to label if $rs == $rt",
                        BasicInstructionFormat.I_BRANCH_FORMAT,
                        "000100 fffff sssss tttttttttttttttt",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement)
                                    throws ProcessingException {

                                int[] operands = statement.getOperands();

                                int rs = RegisterFile.getValue(operands[0]);
                                int rt = RegisterFile.getValue(operands[1]);

                                if (rs == rt) {
                                    
                                    Globals.instructionSet.processBranch(operands[2]);
                                }
                            }
                        }
                )
        );

        instructionList.add(
                new BasicInstruction(
                        "j label",
                        "Jump to label (like MIPS j)",

                        BasicInstructionFormat.I_BRANCH_FORMAT,
                        "000010 00000 00000 ssssssssssssssss",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement)
                                    throws ProcessingException {

                                int[] operands = statement.getOperands();

                                Globals.instructionSet.processBranch(operands[0]);
                            }
                        }
                )
        );



        instructionList.add(
                new BasicInstruction(
                        "syscall",
                        "Invoke a simple system call based on $v0 (like MIPS syscall)",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 00000 00000 00000 001100",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement)
                                    throws ProcessingException {

                                int code = RegisterFile.getValue(2);

                                switch (code) {
                                    case 1: {

                                        int value = RegisterFile.getValue(4);
                                        SystemIO.printString(Integer.toString(value));
                                        break;
                                    }

                                    case 4: {

                                        int addr = RegisterFile.getValue(4);
                                        try {
                                            char ch = (char) Globals.memory.getByte(addr);
                                            while (ch != 0) {
                                                SystemIO.printString(Character.toString(ch));
                                                addr++;
                                                ch = (char) Globals.memory.getByte(addr);
                                            }
                                        } catch (AddressErrorException e) {
                                            throw new ProcessingException(statement, e);
                                        }
                                        break;
                                    }

                                    case 10: {

                                        throw new ProcessingException(
                                                statement,
                                                "Program terminated by syscall 10 (exit)."
                                        );
                                    }

                                    default:
                                        throw new ProcessingException(
                                                statement,
                                                "Unsupported syscall code in SEER language: " + code
                                        );
                                }
                            }
                        }
                )
        );





        instructionList.add(
            new BasicInstruction(
                "drain $t1",
                "Pull 1 point from each positive fate channel into fate",
                BasicInstructionFormat.R_FORMAT,
                "000000 fffff 00000 00000 00000 110100",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement)
                            throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int fateReg = operands[0];
                        int fate = RegisterFile.getValue(fateReg);
                        int pulled = 0;

                        for (int reg = 8; reg <= 15; reg++) {
                            int v = RegisterFile.getValue(reg);
                            if (v > 0) {
                                RegisterFile.updateRegister(reg, v - 1);
                                pulled++;
                            }
                        }

                        RegisterFile.updateRegister(fateReg, fate + pulled);
                    }
                }
            )
        );

   
   

      
        instructionList.add(
            new BasicInstruction(
                "crystal $a0,$a1,$a2",
                "Draw a pixel at (x,y) with colour col",
                BasicInstructionFormat.R_FORMAT,
                "000000 fffff sssss ttttt 00000 110101",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement)
                            throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int x   = RegisterFile.getValue(operands[0]);
                        int y   = RegisterFile.getValue(operands[1]);
                        int col = RegisterFile.getValue(operands[2]);
                        drawPixel(statement, x, y, col);
                    }
                }
            )
        );

       
        instructionList.add(
            new BasicInstruction(
                "seerface $t1",
                "Draw a small fortune-teller avatar using stars as X offset",
                BasicInstructionFormat.R_FORMAT,
                "000000 fffff 00000 00000 00000 110110",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement)
                            throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int xOffset = RegisterFile.getValue(operands[0]);

                        int baseY = 40;    
                        int skin  = 0x00FFFFAA;
                        int robe  = 0x000000FF;
                        int hat   = 0x00AA00FF;

                        try {
                            
                            for (int x = 0; x < 5; x++) {
                                drawPixel(statement, xOffset + x, baseY, hat);
                            }
                            for (int x = 1; x < 4; x++) {
                                drawPixel(statement, xOffset + x, baseY + 1, skin);
                            }
                            for (int y = 2; y < 7; y++) {
                                for (int x = 1; x < 4; x++) {
                                    drawPixel(statement, xOffset + x, baseY + y, robe);
                                }
                            }
                        } catch (ProcessingException e) {
                            throw e;
                        }
                    }
                }
            )
        );

       
        instructionList.add(
            new BasicInstruction(
                "clearveil $a2",
                "Fill the whole crystal ball with a single colour",
                BasicInstructionFormat.R_FORMAT,
                "000000 fffff 00000 00000 00000 110111",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement)
                            throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int col = RegisterFile.getValue(operands[0]);

                        try {
                            for (int y = 0; y < BITMAP_HEIGHT; y++) {
                                for (int x = 0; x < BITMAP_WIDTH; x++) {
                                    drawPixel(statement, x, y, col);
                                }
                            }
                        } catch (ProcessingException e) {
                            throw e;
                        }
                    }
                }
            )
        );

       
        instructionList.add(
            new BasicInstruction(
                "glowrow $a1,$a2",
                "Draw a horizontal aura across a single row",
                BasicInstructionFormat.R_FORMAT,
                "000000 fffff sssss 00000 00000 111000",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement)
                            throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int y   = RegisterFile.getValue(operands[0]);
                        int col = RegisterFile.getValue(operands[1]);

                        try {
                            for (int x = 0; x < BITMAP_WIDTH; x++) {
                                drawPixel(statement, x, y, col);
                            }
                        } catch (ProcessingException e) {
                            throw e;
                        }
                    }
                }
            )
        );

     
        instructionList.add(
            new BasicInstruction(
                "glowcol $a0,$a2",
                "Draw a vertical aura along a single column",
                BasicInstructionFormat.R_FORMAT,
                "000000 fffff sssss 00000 00000 111001",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement)
                            throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int x   = RegisterFile.getValue(operands[0]);
                        int col = RegisterFile.getValue(operands[1]);

                        try {
                            for (int y = 0; y < BITMAP_HEIGHT; y++) {
                                drawPixel(statement, x, y, col);
                            }
                        } catch (ProcessingException e) {
                            throw e;
                        }
                    }
                }
            )
        );
    }

   

    private static void drawPixel(ProgramStatement stmt, int x, int y, int colour)
            throws ProcessingException {
        if (x < 0 || x >= BITMAP_WIDTH || y < 0 || y >= BITMAP_HEIGHT) {
          
            return;
        }
        int offset = (y * BITMAP_WIDTH + x) * 4;
        int addr = BITMAP_BASE + offset;

        try {
            Globals.memory.setWord(addr, colour);
        } catch (AddressErrorException e) {
            throw new ProcessingException(stmt, e);
        }
    }
}

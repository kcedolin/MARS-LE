 .data
yes_msg:    .asciiz "The omens say: YES.\n"
no_msg:     .asciiz "The omens say: NO.\n"
        .text
        .globl main
    # Fate registers (t-registers)
    .eqv $fate      $t0    # main fortune score
    .eqv $aura      $t1    # overall aura / mood
    .eqv $omens     $t2    # omen flags / bitfield
    .eqv $vision    $t3    # temporary “seen future” value
    .eqv $karma     $t4
    .eqv $destiny   $t5
    .eqv $stars     $t6    # used for bitmap x, random, etc.
    .eqv $spirits   $t7
 
    # Seer scratch (s-registers)
    .eqv $seer0     $s0
    .eqv $seer1     $s1
    .eqv $seer2     $s2
    .eqv $seer3     $s3
    .eqv $seer4     $s4
    .eqv $seer5     $s5
    .eqv $seer6     $s6
    .eqv $seer7     $s7
 
    # Crystal ball helpers
    .eqv $orb_x     $a0
    .eqv $orb_y     $a1
    .eqv $orb_col   $a2
    .eqv $chant     $a3
 
    # Return / stack
    .eqv $future    $v0
    .eqv $echo      $v1
    .eqv $veil      $sp
    .eqv $portal    $ra

main:
        # Seed the fortune
        li      $fate, 12345
        # Cast a fortune into [-50, 50]
        cast    $fate          
        # Collapse fortune to 0 (no) or 1 (yes)
        yesno   $fate          
        # Branch based on answer
        beq     $fate, $zero, oracle_no
oracle_yes:
        # Print YES message
        whisper yes_msg
        j       done
oracle_no:
        # Print NO message
        whisper no_msg
done:
        li      $v0, 10       
        syscall

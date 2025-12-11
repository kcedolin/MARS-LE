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
        # Set the crystal ball to dark blue-ish
        # color = 0x00002060 (fits in 16 bits)
        weave    $orb_col, $zero, 0x2060     # ARGB-ish color
        clearveil $orb_col      

        # Draw the seer avatar a bit from the left
        weave    $stars, $zero, 100          
        seerface $stars                 

        # Draw a glowing horizontal line across the ball
        weave    $orb_y, $zero, 120          # row

        # color = 0x00FF00FF (purple aura)
        #uplift $orb_col, 0x0FF
                
        weave    $orb_col, $orb_col, 0x00FF  # lower 16 bits
        # now $orb_col = 0x00FF00FF

        glowrow  $orb_y, $orb_col      

        # Draw a glowing vertical line
        weave    $orb_x, $zero, 256          # middle column (0x0100)
        glowcol  $orb_x, $orb_col  

        # Place a single bright pixel "star" above the seer
        weave    $orb_x, $zero, 108          # near seer face
        weave    $orb_y, $zero, 5

        # color = 0x00FFFF00 (bright yellow)
     #   uplift   $orb_col, 0x00FF            # upper 16 bits
        weave    $orb_col, $orb_col, 0xFF00  # lower 16 bits
        # now $orb_col = 0x00FFFF00

        crystal  $orb_x, $orb_y, $orb_col 

        weave    $v0, $zero, 10
        syscall

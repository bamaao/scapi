/*
 *   MIRACL compiler/hardware definitions - mirdef.h
 */

#define MR_BIG_ENDIAN
#define MIRACL	32
#define mr_utype int

#define mr_unsign32 unsigned int
#define mr_unsign64 unsigned long

#define MR_IBITS 32
#define MR_LBITS 32

#define MR_FLASH 52
#define MAXBASE ((mr_small)1<<(MIRACL-1))

#define MR_GENERIC_MT

#include <stdio.h>

#include "ltl2ba.h"

extern void print_buchi(BState *);
extern void print_spin_buchi(void);
extern char uform[];
extern int hasuform;
extern int b_accept;
extern BState * bstates;

extern FILE *tl_out;
extern int tl_verbose;

extern int mod;
extern int sym_id, sym_size;
extern int cnt, node_id;
extern char **sym_table;
extern int get_sym_id(char *);
extern int *make_set(int , int);
extern int *list_set(int *, int);
extern void tfree(void *);

extern BState * bstateNxt(const BState *);
extern int bstateFinal(const BState *);
extern int bstateId(const BState *);
extern BTrans * bstateFirstTrans(const BState *);

extern BState * btransTo(const BTrans *);
extern void btransPosNeg(const BTrans *, int*[]);
extern BTrans * btransNxt(const BTrans *);


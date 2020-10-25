#include "myltl2ba.h"

/*

typedef struct BTrans {
  struct BState *to;
  int *pos;
  int *neg;
  struct BTrans *nxt;
} BTrans;

*/

BState * btransTo(const BTrans *t){
	return t->to;
}

void btransPosNeg(const BTrans *t, int* buf[]){
	buf[0]=t->pos;
	buf[1]=t->neg;
}

BTrans * btransNxt(const BTrans *t){
	return t->nxt;
}

/*
typedef struct BState {
  struct GState *gstate;
  int id;
  int incoming;
  int final;
  struct BTrans *trans;
  struct BState *nxt;
  struct BState *prv;
} BState;

*/

int bstateId(const BState *s){
	return s->id;
}

int bstateFinal(const BState *s){
	return s->final;
}

BTrans * bstateFirstTrans(const BState *s){
	return s->trans;
}
BState * bstateNxt(const BState *s){
	return s->nxt;
}


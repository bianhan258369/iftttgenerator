#ifndef __MAIN_H_
#define __MAIN_H_

FILE	*tl_out;

int	tl_stats     = 0; /* time and size stats */	
int     tl_simp_log  = 1; /* logical simplification */
int     tl_simp_diff = 1; /* automata simplification */
int     tl_simp_fly  = 1; /* on the fly simplification */
int     tl_simp_scc  = 1; /* use scc simplification */
int     tl_fjtofj    = 1; /* 2eme fj */
int	tl_errs      = 0;
int	tl_verbose   = 0;
int	tl_terse     = 0;
unsigned long	All_Mem = 0;

static char	uform[4096];
static int	hasuform=0, cnt=0;
static char     **ltl_file = (char **)0;
static char     **add_ltl  = (char **)0;
static char     out1[64];

static void	tl_endstats(void);
static void	non_fatal(char *, char *);

#endif
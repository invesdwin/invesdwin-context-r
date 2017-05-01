library("de.invesdwin.context.r.renjin.packages:LSPM");
library(DEoptim);
trades <- c(2,-1);
probs <- c(0.5,0.5);
lspobj <- lsp(trades,probs);
lspobj;
result <- optimalf(lspobj);
result;
lspobj$f <- result$f;  
lspobj;


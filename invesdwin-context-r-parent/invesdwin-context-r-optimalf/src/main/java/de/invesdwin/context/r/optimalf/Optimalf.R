library("de.invesdwin.context.r.renjin.packages.lspm");
trades <- c(2,-1);
probs <- c(0.5,0.5);
lspobj <- lsp(trades,probs);
lspobj;
result <- optimalf(lspobj);
result;
lspobj$f <- result$f;  
lspobj;
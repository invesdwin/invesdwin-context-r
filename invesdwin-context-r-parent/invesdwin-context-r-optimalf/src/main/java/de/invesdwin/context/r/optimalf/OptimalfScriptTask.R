library(LSPM)
library(DEoptim)
trades <- c(2,-1)
probs <- c(0.5,0.5)
lspobj <- lsp(trades,probs)
result <- optimalf(lspobj)
optimalf <- result$f

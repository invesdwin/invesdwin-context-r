library(LSPM)
library(DEoptim)
trades <- c(0.1, -.15, .2, -.1)
#trades <- c(2, -3, 10, -5)
probs <- c(0.25,0.25,0.25,0.25)
lspobj <- lsp(trades,probs)
result <- optimalf(lspobj)
optimalf <- result$f

# Semantics of Promises:
## Cheet Sheat:
$$\begin{align}
& \sigma \in Heap \newline
& \psi \in \text{PromiseState} \newline
& f \in \text{FulfillReactions} \newline
& r \in \text{RejectReactions} \newline
& \pi \in \text{Queue} \newline
& p \in \text{Reaction} \newline
& \Psi \in \text{PromiseValue}
\newline
\newline
& Addr & = & \text{list of addresses} & \newline
& \langle \sigma, \psi, f, r, \pi \rangle & = &  \text{ heap, promise state, list of fulfill reactions, list of reject
reactions, queue of event loop} \newline
& Nil & = & \text{ empty list} \newline
& x :: xs & = & \text{ list concatenation (merging lists)} \newline
& xs ::: ys & = & \text{ represents the append operation (here between two lists)}
\end{align}$$

The **syntax** of the calculus λ has the following expressions:

$$ \begin{flalign}
& \textbf{promisify(e)} \text{ turns an object into a promise} & \newline
& \textbf{e.resolve(e)} \text{ fulfills a promise with a value and causes its resolve reaction to be scheduled for
execution by the event loop} & \newline
& \textbf{e.reject(e)} \text{ rejects a promise with a value and causes its reject reaction to be scheduled for
execution by the event loop} & \newline
& \textbf{e.onResolve(e)} \text{ registers a resolve reaction on a promise and returns a dependant promise} & \newline
& \textbf{e.onReject(e)} \text{ registers a reject reaction on a promise and returns a dependant promise} & \newline
& \textbf{e.link(e)} \text{ registers a dependency between two promises s.t. when the former is resolved/rejected the 
latter is also} & \newline
\end{flalign} $$

$$ \begin{flalign} 
& \text{The} \textbf{ runtime} \text{ of } \lambda_p \text{ has}: &
\end{flalign} $$

$$ \begin{flalign}
& \textbf{prmosie state } \psi \text{: maps each address to an algebraic data type } \psi \in \text{PromiseValue } &
\newline
& \text{which is one of: pending P, fulfilled F(v), rejected R(v), where v is the result value of the promise.} &
\newline
\newline
& \textbf{fulfill reactions f} \text{: maps an address to a list of reaction and dependent promise pairs for a } &
\newline
& \text{pending promise} &
\newline
\newline
& \textbf{reject reactions r} \text{: map an address a to a list of reaction and dependent promise pairs for a } &
\newline
& \text{pending promise} &
\newline
\newline
& \textbf{reaction p} \text{: either a lambda functioin or a special default reaction (identity function)} &
\newline
\newline
& \textbf{queue } \pi \text{: a queue of scheduled reactions, i.e. promises that have been settled for which the } &
\newline
& \text{reactions are awaiting asynchronous computation by the event loop} &
\end{flalign} $$

## Exercise - Semantics of Promise Rules:

### Rule 1:

$$\begin{gather}
a \in Addr & a \in dom(\sigma) & \psi(a) = P \end{gather} $$
$$\begin{gather} 
a' \in Addr & a' \notin dom(\sigma) & \psi' = \psi[a'\mapsto P] & \sigma'=\sigma[a' \mapsto \{\}]
\end{gather} $$
$$\begin{gather} f' \mapsto f[a \mapsto f(a) ::: (\lambda,a')][a' \mapsto Nil] & r'=r[a' \mapsto Nil] \end{gather} $$
---
$$\begin{gather}
\langle \sigma,\psi,f,r,\pi,E[a.onResolve(\lambda)] \rangle \rightarrow \langle \sigma',\psi',f',r',\pi,E[a'] \rangle
\end{gather} $$
---

### Rule 2: 

$$\begin{gather}  a \in Addr & a \in dom(\sigma) & \psi(a) = F(v) \end{gather} $$
$$\begin{gather}
a' \in Addr & a' \notin dom(\sigma) & \psi' = \psi[a'\mapsto F(v)] & \sigma'=\sigma[a' \mapsto \text{{}}]
\end{gather} $$
$$\begin{gather} f' = f[a' \mapsto Nil] & r'=r[a' \mapsto Nil] \newline \end{gather} $$
$$\begin{gather} \pi' = \pi ::: (F(v), \lambda, a') \end{gather} $$
---
$$\begin{gather}
\langle \sigma,\psi,f,r,\pi,E[a.onResolve(\lambda)] \rangle \rightarrow \langle \sigma',\psi',f',r',\pi,E[a'] \rangle
\end{gather} $$
---

### Rule 3:

$$\begin{gather}  a \in Addr & a \in dom(\sigma) & \psi(a) \in \{F(v'),R(v')\} \end{gather} $$

---
$$\begin{gather}
\langle \sigma,\psi,f,r,\pi,E[a.onResolve(v)] \rangle \rightarrow \langle \sigma,\psi,f,r,\pi,E[undef] \rangle
\end{gather} $$
---

### Rule 4:

$$\begin{gather}  a_1 \in Addr & a_1 \in dom(\sigma) & a_2 \in Addr & a_2 \in dom(\sigma) & \psi(a_1) = F(v)
\end{gather} $$
$$\begin{gather} \pi' = \pi ::: (F(v), default, a_2) \end{gather} $$
---
$$\begin{gather}
\langle \sigma,\psi,f,r,\pi,E[a_1.link(a_2)] \rangle \rightarrow \langle \sigma,\psi,f,r,\pi,E[undef] \rangle
\end{gather} $$
---

### Rule 5: 

$$\begin{gather} a \in Addr & a \in dom(\sigma) & a \notin dom(\psi) \end{gather} $$
$$\begin{gather} \psi' = \psi[a \mapsto P] & f' = f[a \mapsto Nil] & r' = r[a \mapsto Nil] \end{gather} $$

---
$$\begin{gather}
\langle \sigma,\psi,f,r,\pi,E[promisify(a)] \rangle \rightarrow \langle \sigma,\psi,f,r,\pi,E[undef] \rangle
\end{gather} $$
---

### Rule 6:
$$\begin{gather} a \in Addr & a \in dom(\sigma) & \psi(a) = P \end{gather} $$
$$\begin{gather}
f(a) = (\lambda_1,a_1)...(\lambda_n,a_n) & \pi'=\pi:::(F(v),\lambda_1,a_1)...(F(v), \lambda_n,a_n)
\end{gather} $$
$$\begin{gather} \psi'=\psi[a \mapsto F(v)] & f'=f[a \mapsto Nil] & r'=r[a \mapsto Nil] \end{gather} $$
---
$$\begin{gather}
\langle \sigma,\psi,f,r,\pi,E[a.resolve(v)] \rangle \rightarrow \langle \sigma,\psi',f',r',\pi',E[undef] \rangle
\end{gather} $$
---

### Rule 7: 
$$\begin{gather}  a_1 \in Addr & a_1 \in dom(\sigma) & a_2 \in Addr & a_2 \in dom(\sigma) & \psi(a_1) = P
\end{gather} $$
$$\begin{gather} 
f' = f[a_1 \mapsto f(a_1) ::: (default, a_2)] & r' = r[a_1 \mapsto r(a_1) ::: (default, a_2)]
\end{gather} $$
---
$$\begin{gather}
\langle \sigma,\psi,f,r,\pi,E[a_1.link(a_2)] \rangle \rightarrow \langle \sigma,\psi,f',r',\pi,E[undef] \rangle
\end{gather} $$
---

#### Explanation:
$$\begin{flalign}
a_1 \in Addr &: a_1 \text{ is the address of an object}  \newline
a_1 \in dom(\sigma) &: a_1 \text{ is allocated in the heap } (\sigma) \newline
a_2 \in Addr &: a_2 \text{ is the address of an object}  \newline
a_2 \in dom(\sigma) &: a_2 \text{ is allocated in the heap } (\sigma) \newline
\newline
f' = f[a_1 \mapsto f(a_1) ::: (default, a_2)]) &: \text{ register fulfill reaction } (default, a_2) \text{ to promise } 
a_1 \newline
r' = r[a_1 \mapsto r(a_1) ::: (default, a_2)] &: \text{ register reject reaction } (default, a_2) \text{ to promise }
a_1 \newline \newline
\langle \sigma,\psi,f,r,\pi,E[a_1.link(a_2)] \rangle \rightarrow \langle \sigma,\psi,f',r',\pi,E[undef] \rangle &:
\text{ link } a_2 \text{ to } a_1 \text{, so when } a_1 \text{ is resolved, all registered } \newline 
&.. \text{reactions will be executed with the identity function, } \newline &.. \text{causing } a_2
\text{ to be resolved/rejected with the same value}
\end{flalign}$$

$$\begin{align}
& \text{In other words: } \newline
& \textbf{This rule causes a promise to be "linked" to another, so when the first is resolved, the second is also}
\textbf{ with the same value.}
\end{align}$$
---


## Task 12 - What kind of bugs can be detected by what kind of situations in a promise graph?

### Question - Connect the right bug to the right situation:
| Bug                                 | Situations in a promise graph |
|-------------------------------------|-------------------------------|
| Missing Resolve/Reject Reaction     |                               |
| Dead Promise                        |                               |
| Missing Exceptional Reject Reaction |                               |
| Double Resolve/Reject               |                               |


| Bug | Situations in a promise graph                               |
|-----|-------------------------------------------------------------|
|     | Multiple resolve/reject edges leading to the same promise   |
|     | A promise that has no outgoing registration edges           |
|     | A promise with a reject edge, but no registration edge      |
|     | A promise with no resolve or reject edges nor any link edge |
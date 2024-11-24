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

## Exercises:

### Rule 1:

$$\begin{gather}
a \in Addr & a \in dom(\sigma) & \psi(a) = P
\end{gather} $$
$$\begin{gather}
a' \in Addr & a' \notin dom(\sigma) & \psi' = \psi[a'\mapsto P] & \sigma'=\sigma[a' \mapsto \{\}]
\end{gather} $$
$$\begin{gather}
f' \mapsto f[a \mapsto f(a) ::: (\lambda,a')][a' \mapsto Nil] & r'=r[a' \mapsto Nil]
\end{gather} $$
---
$$\begin{gather}
\langle \sigma,\psi,f,r,\pi,E[a.onResolve(\lambda)] \rangle \rightarrow \langle \sigma',\psi',f',r',\pi,E[a'] \rangle
\end{gather} $$
---

#### Explanation:
$$\begin{flalign}
a \in Addr &: \text{ a is the address of an object}  \newline
a \in dom(\sigma) &: \text{ a is allocated in the heap } (\sigma) \newline
a' \in Addr &: \text{dependent promise a' is the address of an object} \newline
a' \notin dom(\sigma) &: \text{ allocate a', since it's not in the heap } \sigma \newline \newline
\psi(a) = P &: \text{ the promise has state "pending"} \newline \newline
\psi' = \psi[a' \mapsto P] &: \text{ add state } \psi' \text{ of dependent promise } a' \text{ as "pending"} \newline
\sigma' = \sigma[a' \mapsto \text{{}}] &: \text{ update heap of dependant promiise } a' \text{ by mapping }  a'
\text{ to empty value {}} \newline \newline
\lambda &: \text{ the fulfill reaction} \newline \newline
f' = f[a \mapsto f(a) ::: (\lambda,a')][a' \mapsto Nil] &: \text{ add a fulfill reaction for } a', (λ,a'), 
\text{ to the list of } \text{ fulfill reactions of promise } a \text{, clear fulfil reactions for } a' \newline
r' = r[a \mapsto Nil] &: \text{ clear reject reaction from promise } a \newline \newline
\langle \sigma,\psi,f,r,\pi,E[a.onResolve(\lambda)] \rangle \rightarrow \langle \sigma',\psi',f',r',\pi,E[a'] \rangle &:
\text{Consider } E[a.onResolve(\lambda)] \text{, allocate a dependant promise with address a', initialize its reactions}
\newline &.. \text{ to empty list and add the pair } (\lambda, a') \text{ to the fulfill reactions of original promise a} 
\end{flalign}$$

$$\begin{flalign}
\newline
& \text{When a is eventually resolved the function } \lambda \text{ will be executed asynchronously by the event loop,}
& \text{ and its return value will be used to resolve the dependent promise } a' \newline \newline
& \text{In other words: } \textbf{This rule registers a fulfill reaction on a pending promise.}
\end{flalign}$$
---

### Rule 2: 

$$\begin{gather}  a \in Addr & a \in dom(\sigma) & \psi(a) = F(v)
\end{gather} $$
$$\begin{gather}
a' \in Addr & a' \notin dom(\sigma) & \psi' = \psi[a'\mapsto F(v)] & \sigma'=\sigma[a' \mapsto \text{{}}]
\end{gather} $$
$$\begin{gather}
f' = f[a' \mapsto Nil] & r'=r[a' \mapsto Nil] \newline
\end{gather} $$
$$\begin{gather}
\pi' = \pi ::: (F(v), \lambda, a')
\end{gather} $$
---
$$\begin{gather}
\langle \sigma,\psi,f,r,\pi,E[a.onResolve(\lambda)] \rangle \rightarrow \langle \sigma',\psi',f',r',\pi,E[a'] \rangle
\end{gather} $$
---

#### Explanation:
$$\begin{flalign}
a \in Addr &: \text{ a is the address of an object}  \newline
a \in dom(\sigma) &: \text{ a is allocated in the heap } (\sigma) \newline
a' \in Addr &: \text{dependent promise a' is the address of an object} \newline
a' \notin dom(\sigma) &: \text{ allocate a', since it's not in the heap } \sigma \newline \newline
\psi(a) = F(v) &: \text{ the promise has been fulfilled with a value v according to the promise state map } \psi
\newline \newline
\psi' = \psi[a' \mapsto F(v)] &: \text{ add state } \psi' \text{ of dependent promise } a' \text{ as "resolved" with }
\text{ value v} \newline
\sigma' = \sigma[a' \mapsto \text{{}}] &: \text{ update heap of dependant promiise } a' \text{ by mapping }  a'
\text{ to empty value {}} \newline \newline
f' = f[a' \mapsto Nil] &: \text{ clear fulfill reaction on dependant promise } a' \newline
r' = r[a' \mapsto Nil] &: \text{ clear reject reaction on dependant promise } a' \newline
\newline
\pi' = \pi ::: (F(v), \lambda, a') &: \text{ append the triple } (F(v), \lambda, a') \text{ to the queue} \newline
\newline 
\langle \sigma,\psi,f,r,\pi,E[a.onResolve(\lambda)] \rangle \rightarrow \langle \sigma',\psi',f',r',\pi,E[a'] \rangle &:
\text{Consider } E[a.onResolve(\lambda)] \text{, allocate a dependent promise a', and enqueue (i.e. append) } \newline
&.. (F(v), \lambda, a') \text{ to the queue } \pi \text{ and return a'}
\end{flalign}$$

$$\begin{flalign}
\newline
& \text{The effect of this is that, despite the promise already being resolved, the reaction being registered is }
\text{scheduled for execution.} \newline \newline
& \text{In other words: } \textbf{This rule schedules the reaction being registered of a resolved promise for execution}
\end{flalign}$$
---

### Rule 3:

$$\begin{gather}  a \in Addr & a \in dom(\sigma) & \psi(a) \in \{F(v'),R(v')\}
\end{gather} $$
---
$$\begin{gather}
\langle \sigma,\psi,f,r,\pi,E[a.onResolve(v)] \rangle \rightarrow \langle \sigma,\psi,f,r,\pi,E[undef] \rangle
\end{gather} $$
---
#### Explanation:
$$\begin{flalign}
a \in Addr &: \text{ a is the address of an object}  \newline
a \in dom(\sigma) &: \text{ a is allocated in the heap } (\sigma) \newline \newline
\psi(a) \in \{F(v'),R(v')\} &: \text{the state, } \psi \text{, indicates that the promise is already resolved/rejected} 
\newline \newline
\langle \sigma,\psi,f,r,\pi,E[a.onResolve(\lambda)] \rangle \rightarrow \langle \sigma',\psi',f',r',\pi,E[a'] \rangle &:
\text{Consider } E[a.onResolve(\lambda)] \text{, which have already been resolved/rejected, and add resolve reaction} 
\end{flalign}$$

$$\begin{align}
& \text{In other words: } \textbf{This rule states that resolving a settled promise has no effect because the resolve}
\textbf{ action will never be executed.}
\end{align}$$
---

### Rule 4:

$$\begin{gather}  a_1 \in Addr & a_1 \in dom(\sigma) & a_2 \in Addr & a_2 \in dom(\sigma) & \psi(a_1) = F(v)
\end{gather} $$

$$\begin{gather}
\pi' = \pi ::: (F(v), default, a_2)
\end{gather} $$
---
$$\begin{gather}
\langle \sigma,\psi,f,r,\pi,E[a_1.link(a_2)] \rangle \rightarrow \langle \sigma,\psi,f,r,\pi,E[undef] \rangle
\end{gather} $$
---
#### Explanation:
$$\begin{flalign}
a_1 \in Addr &: a_1 \text{ is the address of an object}  \newline
a_1 \in dom(\sigma) &: a_1 \text{ is allocated in the heap } (\sigma) \newline
a_2 \in Addr &: a_2 \text{ is the address of an object}  \newline
a_2 \in dom(\sigma) &: a_2 \text{ is allocated in the heap } (\sigma) \newline
\newline
\pi' = \pi ::: (F(v), default, a') &: \text{ append the triple } (F(v), default, a') \text{ to the queue} \newline
\newline
\psi(a_1) \in \{F(v'),R(v')\} &: \text{the state of the promise } a \text{ according to the promise state map } \psi
\newline &..\text{indicates that the promise has already been resolved/rejected}
\end{flalign}$$

$$\begin{align}
& \text{In other words: } \textbf{This rule causes an already settled promise to be "linked" to another. Regardless of }
\textbf{their states.}
\end{align}$$
---

### Rule 5: 

$$\begin{gather}  
a \in Addr & a \in dom(\sigma) & a \notin dom(\psi)
\end{gather} $$
$$\begin{gather}  
\psi' = \psi[a \mapsto P] & f' = f[a \mapsto Nil] & r' = r[a \mapsto Nil]
\end{gather} $$

---
$$\begin{gather}
\langle \sigma,\psi,f,r,\pi,E[promisify(a)] \rangle \rightarrow \langle \sigma,\psi,f,r,\pi,E[undef] \rangle
\end{gather} $$
---

#### Explanation:
$$\begin{flalign}
a \in Addr &: a \text{ is the address of an object}  \newline
a \in dom(\sigma) &: a \text{ is allocated in the heap } (\sigma) \newline
a \notin dom(\psi) &: a \text{ has no promise state} \newline \newline
\psi' = \psi[a \mapsto P] &: \text{ register state for promise } a \text{ as "pending"} \newline
f' = f[a \mapsto Nil] &: \text{clear fulfill reaction on promise } a \newline
r' = r[a \mapsto Nil] &: \text{clear reject reaction on promise } a \newline
\newline \newline
\langle \sigma,\psi,f,r,\pi,E[promisify(a)] \rangle \rightarrow \langle \sigma,\psi,f,r,\pi,E[undef] \rangle &:
\text{ promisify() is removed from the queue after it's performed, i.e. converted from promise to undefined}
\end{flalign}$$

$$\begin{align}
& \text{In other words: } \textbf{This rule clears fulfill/reject reactions of a settled promise}
\end{align}$$
---

### Rule 6:
$$\begin{gather}  
\
\end{gather} $$
$$\begin{gather}  
\
\end{gather} $$

---
$$\begin{gather}
\
\end{gather} $$
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
|     | Multiple resolve/reject edges leading to the same promise   |
|     | A promise with a reject edge, but no registration edge      |
|     | A promise with no resolve or reject edges nor any link edge |
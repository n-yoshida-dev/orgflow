## **概要**

本書は、経費申請アプリにおける主要概念の責務分離と関係を整理するための設計概要である。  
 目的は、ER 図、API 設計、認可設計へ進む前に、**どの概念が何を表し、どこに責務を置くか**を明確にすることである。

---

## **設計方針**

本アプリでは、状態、履歴、設定、権限、監査を同一概念に混在させない。  
 特に、以下の分離を重視する。

* request は申請版の現在状態を持つ正本である

* approval は判断履歴であり、進行管理を持たない

* approval policy は承認ルート決定ルールである

* role は操作権限であり、承認段数や金額条件は持たない

* audit log は横断的な証跡であり、業務データ本体ではない

---

## **主要概念**

### **request**

request は、1つの申請版を表す。  
 申請内容、現在状態、現在承認段を保持する。  
 また、申請時点で確定した承認担当情報や適用済み承認ルートを参照する。

### **approval**

approval は、request に対する判断履歴である。  
 誰が、いつ、どのような判断をしたかを表す。  
 approval は進行中状態を持たず、承認ワークフローの現在位置は request 側で管理する。

### **approval policy**

approval policy は、承認ルートを決定するルールである。  
 所属 organization、申請種別、金額条件などに応じて、承認段数や承認対象者の条件を決定する。

### **applied approval route**

applied approval route は、approval policy を request 作成時に評価した結果である。  
 同じ policy が後で変更されても、既存 request には申請時点で確定した route を維持する。

### **organization**

organization は、user の所属単位であり、データ閲覧境界と権限適用範囲の基準である。

### **role**

role は、organization への所属に対して付与される操作権限の区分である。  
 role は、申請作成、承認、管理操作といった「何ができるか」を表す。  
 一方で、承認段数や金額条件などの業務ルールは approval policy 側で扱う。

### **audit log**

audit log は、重要操作の証跡を横断的に記録する概念である。  
 request や approval の代替ではなく、追跡可能性を担保するための独立概念として扱う。

---

## **概念同士の関係**

### **user と organization**

user と organization は多対多である。  
 user は複数の organization に所属でき、organization には複数の user が所属できる。

### **user と role**

user と role は直接結ばない。  
 role は、user の organization 所属に対して付与される。

### **request と approval**

1つの request に対して、approval は 0 件以上発生する。  
 approval は request の判断履歴であり、request の現在状態そのものではない。

### **request と approval policy / applied approval route**

request 作成時には、その時点の approval policy を参照して承認ルートを決定する。  
 request 自体が過去の policy を毎回動的に参照するのではなく、申請時点で確定した適用結果を保持または参照する。

### **audit log と各概念**

audit log は、request / approval / approval policy / user などの対象識別子を保持する。  
 これにより、どの操作がどの対象に対して行われたかを追跡できる。

---

## **認可の考え方**

認可判定では、少なくとも以下を確認する。

* 認証済み user であること

* 対象 request が属する organization において必要な role を持つこと

* その request の現在承認段の対象者であること

* その request に適用された承認ルート上の承認対象者であること

認可は UI だけでなく、API 側でも強制される前提とする。これはロードマップでも「UIで隠せば権限制御できたと誤解しないこと」「API側の 403 と整合して初めて意味がある」と整理されている論点です。

---

## **設計上の主要判断**

以下は、README や ADR に残すべき判断候補である。

* 題材として経費申請を採用した理由

* request と approval を分離した理由

* role と approval policy を分離した理由

* 役職を認可の根拠にしない理由

* request を版管理する理由

* 監査ログを first-class の独立概念として扱う理由

---

## **未確定論点**

* applied approval route をどの粒度で保持するか

* organization 所属をどのような中間概念で表すか

* 閲覧権限の細かい条件をどこまで role で表すか

* 代理承認、代理申請を Phase 1 に含めるか

* 同一承認段の複数承認者に主担当概念を入れるか

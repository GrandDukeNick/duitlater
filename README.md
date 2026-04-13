# DuitLater - Malaysia's Most Trailing Deferred Payment Orchestration Service

> *"Because paying now is so 2026."*

**Now accepting DPP (Deferred Payments Platform) participants!** 🚀

---

## 🏦 The Origin Story

One day, a banker looked at DuitNow and thought: *"This is too fast. What if we did the opposite?"*

And thus, **DuitLater** was born.

| DuitNow | DuitLater |
|---------|-----------|
| "Pay now, stress now" | "Pay later, stress later" |
| Real-time (too fast) | Deferred (just right) |
| Instant regret | Scheduled regret |
| For impatient people | For procrastinators |

**Tagline:** *"Why pay today when you can pay tomorrow? Or next week. We don't judge."*

---

## 🎯 What We're Looking For

**DPP (Deferred Payments Platform) Participants Wanted!**

Join the revolution of paying... eventually.

### Benefits of Becoming a DPP Member:

| Benefit | Description |
|---------|-------------|
| ⏰ **Ultimate Flexibility** | Schedule payments for any date. Even 2030. We'll remind you. Probably. |
| 💱 **Forex Confusion** | Convert USD to MYR at rates that may or may not make sense |
| 🔐 **Security Theater** | We have JWT. It looks important. |
| 📊 **Pagination** | 10 records per page. Count 'em. |
| 🎭 **API Documentation** | Exists. Somewhere. |

### Requirements to Join:

- Must have a bank account (or know someone who does)
- Must understand what "deferred" means (dictionary available upon request)
- Must not ask "why" too many times

---

## 🗄️ Database Design: A Masterclass in Overthinking

### The Great UUID Debate

We spent 47 hours discussing whether to use `BIGINT` or `UUID`. Here's what we concluded:

| Approach | Status |
|----------|--------|
| `BIGINT IDENTITY` | Too predictable. Like counting to infinity. |
| `UUID` | Random enough to impress your friends |
| `BOTH` | We tried. It got weird. |

**Winner:** UUID (because `715db132-012c-47b8-86c8-a169853eaae3` looks more important than `1`)

### Index Strategy: The Less is More Philosophy

Some people index everything. We don't.

| Index | Why it exists |
|-------|---------------|
| `IDX_TRX_CUST` | So your query doesn't take a coffee break |
| `IDX_TRX_UUID` | Because someone might actually look up a transaction |
| `IDX_TRX_IDEMP` | To prevent you from paying twice (you're welcome) |

**Not indexed:** Your hopes and dreams.

### Foreign Keys: We Have Them

Technically. They might work. Probably.

---

## 🔐 Security: Because Someone Might Actually Use This

### JWT Authentication
Our JWT implementation features:
- 24-hour expiration (plenty of time to forget it)
- Role-based access (ADMIN — more to come™)
- BCrypt hashing (so secure even we can't read your password)

### The Admin Experience

Default credentials: `admin / admin123`

*"Change your password"* — said no assessment ever.

---

## 🌐 External API: The "Nested Call" They Asked For

We call a free Forex API. It works 60% of the time, every time.
They're probably accurate. Probably.

## 📊 Pagination: The Feature Nobody Asked For But Everyone Got

**10 records per page.**

- Page 1: Records 1-10
- Page 2: Records 11-20
- Page 3: Records 21-30 (if you have that many friends)

*"But why 10?"* — Someone asked.
*"Because the assignment said so."* — We replied.

---

## 🧪 Testing Strategy

| Type | Status |
|------|--------|
| Unit Tests | Planned™ |
| Integration Tests | In the clouds |
| Production Testing | You are here |

---

## 🚀 How to Join DPP

### Step 1: Clone this repository
```bash
git clone https://github.com/granddukenick/duitlater.git
cd duitlater
```
### Step 2: Run the schema (pray it works)
```bash
sqlcmd -S localhost -U sa -P YourPasswordHere1! -i init.sql
sqlcmd -S localhost -U sa -P YourPasswordHere1! -i dummy_data.sql
```
### Step 3: Start the app
```bash
mvn spring-boot:run
```

### Step 4: Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
-H "Content-Type: application/json" \
-d '{"username":"admin","password":"admin123"}'
```

### Step 5: Schedule a payment you'll regret later (use your own uuids..)
```bash
curl -X POST http://localhost:8080/api/transactions/schedule \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{"customerUuid":"715db132-012c-47b8-86c8-a169853eaae3",...}'
```

### Step 6: Wait


## 📞 Support
**Q**: Why is it called DuitLater?

**A**: Because DuitEventually was too long.


**Q**: Does this actually move money?

**A**: No. It just thinks about it really hard.


**Q**: Can I use this for real payments?

**A**: We strongly advise against it. Strongly.


**Q**: Will this pass the interview?

**A**: That's up to you, brave developer.

## 🏆 Acknowledgments
- **DuitNow** for existing (sorry we made fun of you)
- **Frankfurter API** for being free (please don't block us)
- **Coffee** for getting us through the UUID debate
- **The Interviewer** for reading this far

## 📜 License
**DPL (Deferred Payment License)** — You promise to pay attention later

---

Built with ❤️ and ☕ by someone who really needs this job.

**Apply for DPP membership today! Limited spots available. Terms and conditions apply. No refunds.**

---

## P.S.

This README is 50% satire, 40% caffeine, and 10% actual documentation.


# Febs-identify

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/cn.brainpoint/febs-identify/badge.svg)](https://maven-badges.herokuapp.com/maven-central/cn.brainpoint/febs-identify/)
[![License](https://img.shields.io/github/license/brainpoint/febs-identify-java)](https://opensource.org/licenses/MIT)

Distributed Unique Identify.

- like objectId, a process can generator id `2^24-1` per second.
- Container `20` chars in id
- Use database to assign unique machineId.

## How to use

maven config.

```html
<dependency>
    <groupId>cn.brainpoint</groupId>
    <artifactId>febs-identify</artifactId>
    <version>0.0.1</version>
</dependency>
```

Generate the ID using the following code:

```java
import cn.brainpoint.febs.identify.*;

String uniqueId = Identify.nextId();
```

### Initialize

```java
import cn.brainpoint.febs.identify.*;

Identify.initialize(new IdentifyCfg(
    "mysql",
    "localhost:3306/xx",
    "username",
    "password",
));
```

Use database to assign unique `machineId`. It will use '_distribute_machineId' to named table if not specify a table name.

This method can be invoked multiple times in response to dynamic changes to the application configuration.

### Generate a New Id

```java
String id = Identify.nextId();
```

### Valid Id

```java
assert Identify.isValid(id);
```

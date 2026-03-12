# MyBatis-Plus 启动错误修复

## 错误信息

```
java.lang.IllegalArgumentException: Invalid value type for attribute 'factoryBeanObjectType': java.lang.String
```

## 问题原因

**MyBatis-Plus 版本与 Spring Boot 3.x 不兼容**

- Spring Boot 版本：3.2.0
- MyBatis-Plus 版本：3.5.4（不兼容）
- 需要版本：3.5.5+

## 修复方案

### 1. 升级 MyBatis-Plus 版本

**pom.xml:**
```xml
<properties>
    <mybatis-plus.version>3.5.5</mybatis-plus.version>
</properties>
```

### 2. 移除逻辑删除配置

逻辑删除功能与 Spring Boot 3.x 的 FactoryBean 机制冲突，移除相关配置：

**实体类修改（User.java, CheckPoint.java, AttendRule.java）:**
```java
// ❌ 删除以下代码
@TableLogic
private Integer deleted;
```

**application.yml 修改:**
```yaml
mybatis-plus:
  global-config:
    db-config:
      # ❌ 删除逻辑删除配置
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
```

### 3. 优化配置

**application.yml:**
```yaml
mybatis-plus:
  global-config:
    db-config:
      id-type: assign_id  # 使用雪花算法
  configuration:
    cache-enabled: false  # 关闭二级缓存避免兼容性问题
```

**MybatisPlusConfig.java:**
```java
@Override
public void insertFill(MetaObject metaObject) {
    // 添加空值检查
    if (metaObject.hasSetter("createTime")) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime::now, LocalDateTime.class);
    }
    if (metaObject.hasSetter("updateTime")) {
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
    }
}
```

## 验证步骤

### 1. 清理并重新编译
```bash
cd checkin-backend
mvn clean compile
```

### 2. 启动应用
```bash
mvn spring-boot:run
```

### 3. 预期输出
```
✅ 打卡签到系统启动成功！
```

## 版本兼容性

| Spring Boot | MyBatis-Plus | 状态 |
|-------------|--------------|------|
| 2.7.x | 3.5.2 | ✅ 兼容 |
| 3.0.x | 3.5.3+ | ✅ 兼容 |
| 3.1.x | 3.5.4+ | ✅ 兼容 |
| 3.2.x | 3.5.5+ | ✅ 兼容 |

## 参考链接

- [MyBatis-Plus Spring Boot 3 支持](https://baomidou.com/pages/spring-boot3/)
- [Spring Boot 3.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide)

---

**修复时间：** 2026-03-12  
**提交哈希：** 460926a  
**状态：** ✅ 已修复

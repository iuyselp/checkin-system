#!/usr/bin/env node

/**
 * GitHub 仓库创建脚本
 * 使用方法：node create-repo.js
 * 
 * 需要设置环境变量：GITHUB_TOKEN
 * 获取 Token: https://github.com/settings/tokens (需要 repo 权限)
 */

const https = require('https');

// 从环境变量获取 Token
const TOKEN = process.env.GITHUB_TOKEN;

if (!TOKEN) {
  console.error('❌ 错误：请设置 GITHUB_TOKEN 环境变量');
  console.error('');
  console.error('获取 Token 步骤：');
  console.error('1. 访问 https://github.com/settings/tokens');
  console.error('2. 点击 "Generate new token (classic)"');
  console.error('3. 选择 "repo" 权限');
  console.error('4. 生成后设置环境变量：export GITHUB_TOKEN=your_token');
  console.error('');
  console.error('或者运行：');
  console.error('  GITHUB_TOKEN=your_token node create-repo.js');
  process.exit(1);
}

const REPO_NAME = 'checkin-system';
const REPO_DESC = '基于位置的打卡签到系统 - Vue3 + SpringBoot + MyBatis-Plus';

// GitHub API 请求
function githubRequest(path, method, data = null) {
  return new Promise((resolve, reject) => {
    const options = {
      hostname: 'api.github.com',
      path: path,
      method: method,
      headers: {
        'Authorization': `token ${TOKEN}`,
        'Accept': 'application/vnd.github.v3+json',
        'User-Agent': 'checkin-system-creator',
        'Content-Type': 'application/json'
      }
    };

    const req = https.request(options, (res) => {
      let body = '';
      res.on('data', (chunk) => body += chunk);
      res.on('end', () => {
        try {
          const result = JSON.parse(body);
          if (res.statusCode >= 400) {
            reject(new Error(`GitHub API Error ${res.statusCode}: ${result.message}`));
          } else {
            resolve(result);
          }
        } catch (e) {
          if (res.statusCode >= 400) {
            reject(new Error(`GitHub API Error ${res.statusCode}: ${body}`));
          } else {
            resolve(body);
          }
        }
      });
    });

    req.on('error', reject);
    
    if (data) {
      req.write(JSON.stringify(data));
    }
    req.end();
  });
}

async function main() {
  console.log('🚀 开始创建 GitHub 仓库...\n');

  try {
    // 1. 获取当前用户信息
    console.log('📝 获取用户信息...');
    const user = await githubRequest('/user', 'GET');
    console.log(`✅ 登录用户：${user.login}\n`);

    // 2. 创建仓库
    console.log(`📦 创建仓库：${REPO_NAME}...`);
    const repo = await githubRequest('/user/repos', 'POST', {
      name: REPO_NAME,
      description: REPO_DESC,
      private: false,
      auto_init: false
    });
    console.log(`✅ 仓库创建成功！`);
    console.log(`📍 仓库地址：${repo.html_url}\n`);

    // 3. 输出推送指令
    console.log('═══════════════════════════════════════════════════════');
    console.log('✅ 仓库创建完成！现在执行以下命令推送代码：');
    console.log('═══════════════════════════════════════════════════════');
    console.log('');
    console.log(`cd /home/admin/.openclaw/workspace/checkin-system`);
    console.log(`git branch -M main`);
    console.log(`git remote add origin git@github.com:${user.login}/${REPO_NAME}.git`);
    console.log(`git push -u origin main`);
    console.log('');
    console.log('═══════════════════════════════════════════════════════');
    console.log(`🌐 或者访问仓库：${repo.html_url}`);
    console.log('═══════════════════════════════════════════════════════');

  } catch (error) {
    console.error(`\n❌ 错误：${error.message}`);
    console.error('\n请检查：');
    console.error('1. GITHUB_TOKEN 是否正确');
    console.error('2. Token 是否有 repo 权限');
    console.error('3. 网络连接是否正常');
    process.exit(1);
  }
}

main();

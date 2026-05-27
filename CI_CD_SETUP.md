# CI/CD Setup

This project now has a GitHub Actions workflow at `.github/workflows/ci-cd.yml`.

## What It Does

- Builds the Maven WAR with JDK 17.
- Uploads the WAR as a GitHub Actions artifact.
- Deploys to a Tomcat server by SSH when deploy secrets are configured.

## Required GitHub Secrets For Deployment

Add these in GitHub: `Settings` -> `Secrets and variables` -> `Actions`.

| Secret | Example | Purpose |
| --- | --- | --- |
| `DEPLOY_HOST` | `your-server-ip` | Server host or IP |
| `DEPLOY_USER` | `ubuntu` | SSH user |
| `DEPLOY_SSH_KEY` | private key content | SSH private key for the server |
| `TOMCAT_WEBAPPS_PATH` | `/opt/tomcat/webapps` | Tomcat `webapps` directory |
| `TOMCAT_SERVICE_NAME` | `tomcat` | Systemd service name |

Optional repository variable:

| Variable | Default | Purpose |
| --- | --- | --- |
| `APP_CONTEXT` | `SpringEventBookingPlatform` | Tomcat context path and WAR name |

## How To Run

- Pull requests: build only.
- Push to `main` or `master`: build, then deploy if all deploy secrets exist.
- Manual run: open `Actions` -> `CI/CD` -> `Run workflow`.

## Output

The build artifact is:

```text
SpringEventBookingPlatform.war
```

After deployment, the expected URLs are:

```text
http://<server-host>:8080/SpringEventBookingPlatform/
http://<server-host>:8080/SpringEventBookingPlatform/swagger-ui/index.html
http://<server-host>:8080/SpringEventBookingPlatform/v3/api-docs
```

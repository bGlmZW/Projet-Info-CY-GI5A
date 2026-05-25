# JavaFX Setup Guide — LifeLine Project

All "cannot be resolved" errors in the UI files will **disappear** once you
follow these 3 steps.

---

## Step 1 — Download JavaFX SDK

1. Go to: **https://gluonhq.com/products/javafx/**
2. Choose:
   - Version: **21.0.x LTS** (stable, works with Java 17–25)
   - Operating System: **Windows**
   - Architecture: **x64**
   - Type: **SDK**
3. Click **Download** → you get a `.zip` file (~35 MB)

---

## Step 2 — Extract the SDK

1. Right-click the downloaded zip → **Extract All**
2. Extract to: `C:\javafx-sdk-21`
3. After extraction you should see:
   ```
   C:\javafx-sdk-21\
   ├── lib\
   │   ├── javafx.controls.jar
   │   ├── javafx.graphics.jar
   │   └── ... (other jars)
   └── legal\
   ```

---

## Step 3 — Configure your IDE

### If you use VSCode / Antigravity IDE

The `.vscode/settings.json` and `.vscode/launch.json` files are already
created in this project. **You just need to verify the path matches.**

Open `.vscode/settings.json` and confirm this line matches your extraction path:
```json
"java.project.referencedLibraries": [
    "C:/javafx-sdk-21/lib/**/*.jar"
]
```

Open `.vscode/launch.json` and confirm:
```json
"vmArgs": "--module-path \"C:/javafx-sdk-21/lib\" --add-modules javafx.controls,javafx.graphics"
```

Then:
1. Press **Ctrl + Shift + P** → `Java: Clean Java Language Server Workspace`
2. Click **Restart** when prompted
3. Wait ~10 seconds for the IDE to re-index — **all red errors will disappear**

### If you use Eclipse

1. **Window → Preferences → Java → Build Path → User Libraries**
2. Click **New** → name it `JavaFX21` → OK
3. Select `JavaFX21` → click **Add External JARs**
4. Navigate to `C:\javafx-sdk-21\lib\` and select **all `.jar` files** → Open
5. Right-click your project → **Build Path → Add Library → User Library → JavaFX21**
6. Right-click your project → **Run As → Run Configurations**
7. Go to **Arguments** tab → in **VM Arguments** add:
   ```
   --module-path "C:\javafx-sdk-21\lib" --add-modules javafx.controls,javafx.graphics
   ```

---

## Step 4 — Run the project

- **VSCode/Antigravity**: Press **F5** (or Run → Start Debugging)  
  → uses the `Run LifeLine` configuration from `.vscode/launch.json`
- **Eclipse**: Run → Run As → Java Application → select `fr.projet.Main`

You should see a window with the road network and 5 ambulances.  
Press **▶ Play** to start the simulation.

---

## Troubleshooting

| Error | Fix |
|-------|-----|
| `Error: JavaFX runtime components are missing` | VM args missing or wrong path in launch.json |
| `javafx cannot be resolved` | settings.json path wrong, or IDE not reloaded |
| Blank/black canvas | Run with F5, not with the plain ▶ Run button |

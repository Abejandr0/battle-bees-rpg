let isGameOver = false;
let previousEnemyHealth = -1;
let previousPlayerHealth = -1;

async function startGame(heroClass) {
    document.getElementById('introOverlay').style.display = 'none';
    document.getElementById('gameContainer').style.display = 'block';
    
    // Attempt to play audio
    const bgMusic = document.getElementById('bgMusic');
    try {
        bgMusic.volume = 0.3;
        bgMusic.play();
    } catch (e) { console.log('Audio autoplay blocked', e); }

    await fetch(`/api/start?heroClass=${heroClass}`, { method: 'POST' });
    isGameOver = false;
    previousEnemyHealth = -1;
    previousPlayerHealth = -1;
    await fetchState();
}

async function restartGame() {
    isGameOver = false;
    document.getElementById('introOverlay').style.display = 'flex';
    document.getElementById('gameContainer').style.display = 'none';
    document.getElementById('combatLog').innerHTML = '';
}

async function takeAction(action, target) {
    if (isGameOver && action !== 'start') return;
    
    if (action === 'attack') {
        const sfx = document.getElementById('sfxAttack');
        try {
            sfx.currentTime = 0;
            sfx.volume = 0.5;
            sfx.play();
        } catch(e){}
    }

    await fetch(`/api/action?action=${action}&target=${target}`, { method: 'POST' });
    await fetchState();
}

async function fetchState() {
    const response = await fetch('/api/state');
    const state = await response.json();
    
    if (state.player && state.enemy) {
        updateCombatant('player', state.player);
        updateCombatant('enemy', state.enemy);
        updateLog(state.log);
        
        if (state.player.isDead || state.enemy.isDead) {
            isGameOver = true;
        }
    }
}

function updateCombatant(side, data) {
    document.getElementById(`${side}Name`).innerText = data.name;
    document.getElementById(`${side}Class`).innerText = data.heroClass;
    
    const healthPercent = Math.max(0, (data.health / data.maxHealth) * 100);
    const healthBar = document.getElementById(`${side}HealthBar`);
    healthBar.style.width = `${healthPercent}%`;
    document.getElementById(`${side}HealthText`).innerText = `${data.health}/${data.maxHealth}`;
    
    // Color logic
    if (healthPercent > 60) healthBar.style.backgroundColor = 'var(--health-high)';
    else if (healthPercent > 25) healthBar.style.backgroundColor = 'var(--health-medium)';
    else healthBar.style.backgroundColor = 'var(--health-low)';

    // Damage flash animation
    if (side === 'enemy' && previousEnemyHealth !== -1 && data.health < previousEnemyHealth) {
        flashDamage(side);
    }
    if (side === 'player' && previousPlayerHealth !== -1 && data.health < previousPlayerHealth) {
        flashDamage(side);
    }
    
    if (side === 'enemy') previousEnemyHealth = data.health;
    if (side === 'player') previousPlayerHealth = data.health;

    document.getElementById(`${side}Atk`).innerText = data.attack;
    document.getElementById(`${side}Def`).innerText = data.defense;
    document.getElementById(`${side}Strategy`).innerText = data.strategy;

    const buffsContainer = document.getElementById(`${side}Buffs`);
    buffsContainer.innerHTML = '';
    data.buffs.forEach(buff => {
        const span = document.createElement('span');
        span.className = 'buff-badge';
        span.innerText = buff;
        buffsContainer.appendChild(span);
    });

    const equipContainer = document.getElementById(`${side}Equipment`);
    equipContainer.innerHTML = '';
    data.equipment.forEach(eq => {
        const span = document.createElement('span');
        span.className = 'equip-badge';
        span.innerText = eq;
        equipContainer.appendChild(span);
    });
}

function flashDamage(side) {
    const el = document.getElementById(`${side}Side`);
    el.classList.add('damage-flash');
    setTimeout(() => el.classList.remove('damage-flash'), 400);
}

function updateLog(logs) {
    const logContainer = document.getElementById('combatLog');
    logContainer.innerHTML = '';
    logs.forEach(log => {
        const div = document.createElement('div');
        div.className = 'log-entry';
        if (log.includes('CRITICAL')) div.classList.add('critical');
        if (log.includes('damage') || log.includes('FAILED')) div.classList.add('damage');
        if (log.includes('Stimpack') || log.includes('Shield')) div.classList.add('buff');
        div.innerText = log;
        logContainer.appendChild(div);
    });
    logContainer.scrollTop = logContainer.scrollHeight;
}

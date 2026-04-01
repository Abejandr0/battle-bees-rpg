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

    const response = await fetch(`/api/action?action=${action}&target=${target}`, { method: 'POST' });
    const result = await response.json();
    
    if (result.events) {
        processEvents(result.events);
    }
    await fetchState();
}

let lastCritAttacker = null;

function processEvents(events) {
    events.forEach(evt => {
        // CriticalHitEvent
        if (evt.heroName && !evt.buffName) {
            lastCritAttacker = evt.heroName;
        }
        
        // BuffAppliedEvent
        if (evt.heroName && evt.buffName) {
            const side = evt.heroName.includes('Player') ? 'player' : 'enemy';
            showFloatingText(side, `+${evt.buffName}`, 'dmg-buff');
        }
        
        // HeroAttackedEvent
        if (evt.attackerName && evt.damage !== undefined) {
            const attackerSide = evt.attackerName.includes('Player') ? 'player' : 'enemy';
            const defenderSide = attackerSide === 'player' ? 'enemy' : 'player';
            
            triggerAttackAnimation(attackerSide);
            
            let dmgClass = 'dmg-normal';
            if (lastCritAttacker === evt.attackerName) {
                dmgClass = 'dmg-critical';
                flashDamage(defenderSide);
            }
            showFloatingText(defenderSide, `-${evt.damage}`, dmgClass);
            
            lastCritAttacker = null;
        }
    });
}

function triggerAttackAnimation(side) {
    const visual = document.getElementById(`${side}Visual`);
    if (!visual) return;
    visual.classList.remove('anim-slash', 'anim-projectile', 'anim-dash');
    void visual.offsetWidth; // trigger reflow
    
    const hClass = document.getElementById(`${side}Class`).innerText || '';
    if(hClass.includes('Mage')) visual.classList.add('anim-projectile');
    else if(hClass.includes('Assassin')) visual.classList.add('anim-dash');
    else visual.classList.add('anim-slash');
}

function showFloatingText(side, text, typeClass) {
    const container = document.getElementById(`${side}DamageContainer`);
    if (!container) return;
    const el = document.createElement('div');
    el.className = `floating-text ${typeClass}`;
    el.innerText = text;
    container.appendChild(el);
    setTimeout(() => { el.remove(); }, 2000);
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
    
    if (healthPercent > 60) healthBar.style.backgroundColor = 'var(--health-high)';
    else if (healthPercent > 25) healthBar.style.backgroundColor = 'var(--health-medium)';
    else healthBar.style.backgroundColor = 'var(--health-low)';

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

    updateVisualLayers(side, data);
}

function updateVisualLayers(side, data) {
    const baseLayer = document.getElementById(`${side}BaseLayer`);
    const weaponLayer = document.getElementById(`${side}WeaponLayer`);
    const buffOverlay = document.getElementById(`${side}BuffOverlay`);
    
    if(!baseLayer) return;

    let baseImg = 'warrior_base.png';
    if(data.heroClass.includes('Mage')) baseImg = 'mage_base.png';
    if(data.heroClass.includes('Assassin')) baseImg = 'assassin_base.png';
    if(side === 'enemy') {
       if(data.heroClass.includes('Commander')) baseImg = 'enemy_bee_commander.png';
       else baseImg = 'enemy_bee_soldier.png';
    }
    
    if(side === 'player') baseLayer.src = `assets/characters/${baseImg}`;
    else baseLayer.src = `assets/enemies/${baseImg}`;

    weaponLayer.style.display = 'none';
    
    data.equipment.forEach(eq => {
        if(eq.includes('Rifle') || eq.includes('Sword') || eq.includes('Staff') || eq.includes('Dagger')) {
            weaponLayer.src = `assets/equipment/sword_01.png`;
            if(eq.includes('Staff')) weaponLayer.src = `assets/equipment/magic_staff_01.png`;
            if(eq.includes('Dagger')) weaponLayer.src = `assets/equipment/dagger_01.png`;
            weaponLayer.style.display = 'block';
        }
    });

    if(data.buffs.length > 0) {
        buffOverlay.style.display = 'block';
        buffOverlay.classList.add('buff-glow');
    } else {
        buffOverlay.style.display = 'none';
        buffOverlay.classList.remove('buff-glow');
    }
}

function flashDamage(side) {
    const el = document.getElementById(`${side}Side`);
    if(!el) return;
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
        if (log.includes('Stimpack') || log.includes('Shield') || log.includes('Instinct')) div.classList.add('buff');
        div.innerText = log;
        logContainer.appendChild(div);
    });
    logContainer.scrollTop = logContainer.scrollHeight;
}

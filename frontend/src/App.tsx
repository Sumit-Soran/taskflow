import React, { useState, useEffect } from 'react';
import api from './api';
import { LogOut, Plus, Trash2, CheckCircle2, Clock, AlertTriangle, Layers, Calendar } from 'lucide-react';

interface User {
  id: number;
  name: string;
  email: string;
}

interface Project {
  id: number;
  name: string;
  description?: string;
}

interface Task {
  id: number;
  title: string;
  description?: string;
  status: 'TODO' | 'IN_PROGRESS' | 'DONE';
  priority: 'LOW' | 'MEDIUM' | 'HIGH';
  dueDate?: string;
  assignee?: User;
}

interface Summary {
  totalTasks: number;
  todoCount: number;
  inProgressCount: number;
  doneCount: number;
  overdueCount: number;
}

export default function App() {
  const [token, setToken] = useState<string | null>(localStorage.getItem('token'));
  const [user, setUser] = useState<{ name: string; email: string } | null>(
    localStorage.getItem('user') ? JSON.parse(localStorage.getItem('user')!) : null
  );

  // Auth Form State
  const [isRegister, setIsRegister] = useState(false);
  const [authName, setAuthName] = useState('');
  const [authEmail, setAuthEmail] = useState('');
  const [authPassword, setAuthPassword] = useState('');
  const [authError, setAuthError] = useState('');

  // Dashboard State
  const [projects, setProjects] = useState<Project[]>([]);
  const [selectedProjectId, setSelectedProjectId] = useState<number | null>(null);
  const [tasks, setTasks] = useState<Task[]>([]);
  const [summary, setSummary] = useState<Summary | null>(null);

  // Modals & Forms
  const [showProjectModal, setShowProjectModal] = useState(false);
  const [newProjectName, setNewProjectName] = useState('');
  const [newProjectDesc, setNewProjectDesc] = useState('');

  const [showTaskModal, setShowTaskModal] = useState(false);
  const [taskTitle, setTaskTitle] = useState('');
  const [taskDesc, setTaskDesc] = useState('');
  const [taskPriority, setTaskPriority] = useState<'LOW' | 'MEDIUM' | 'HIGH'>('MEDIUM');
  const [taskDueDate, setTaskDueDate] = useState('');

  // Filter & Search
  const [priorityFilter, setPriorityFilter] = useState<string>('ALL');
  const [searchQuery, setSearchQuery] = useState('');

  useEffect(() => {
    if (token) {
      loadProjects();
    }
  }, [token]);

  useEffect(() => {
    if (selectedProjectId) {
      loadTasksAndSummary(selectedProjectId);
    } else {
      setTasks([]);
      setSummary(null);
    }
  }, [selectedProjectId, priorityFilter]);

  const handleAuth = async (e: React.FormEvent) => {
    e.preventDefault();
    setAuthError('');
    try {
      const endpoint = isRegister ? '/auth/register' : '/auth/login';
      const payload = isRegister
        ? { name: authName, email: authEmail, password: authPassword }
        : { email: authEmail, password: authPassword };
      
      const res = await api.post(endpoint, payload);
      localStorage.setItem('token', res.data.token);
      localStorage.setItem('user', JSON.stringify({ name: res.data.name, email: res.data.email }));
      setToken(res.data.token);
      setUser({ name: res.data.name, email: res.data.email });
    } catch (err: any) {
      setAuthError(err.response?.data?.message || 'Authentication failed');
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setToken(null);
    setUser(null);
    setSelectedProjectId(null);
  };

  const loadProjects = async () => {
    try {
      const res = await api.get('/projects');
      setProjects(res.data);
      if (res.data.length > 0 && !selectedProjectId) {
        setSelectedProjectId(res.data[0].id);
      }
    } catch (err) {
      console.error(err);
    }
  };

  const loadTasksAndSummary = async (pId: number) => {
    try {
      let url = `/projects/${pId}/tasks?size=100`;
      if (priorityFilter !== 'ALL') {
        url += `&priority=${priorityFilter}`;
      }
      const [tasksRes, summaryRes] = await Promise.all([
        api.get(url),
        api.get(`/projects/${pId}/summary`)
      ]);
      setTasks(tasksRes.data.content);
      setSummary(summaryRes.data);
    } catch (err) {
      console.error(err);
    }
  };

  const createProject = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const res = await api.post('/projects', { name: newProjectName, description: newProjectDesc });
      setProjects([...projects, res.data]);
      setSelectedProjectId(res.data.id);
      setShowProjectModal(false);
      setNewProjectName('');
      setNewProjectDesc('');
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to create project');
    }
  };

  const createTask = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedProjectId) return;
    try {
      await api.post(`/projects/${selectedProjectId}/tasks`, {
        title: taskTitle,
        description: taskDesc,
        priority: taskPriority,
        dueDate: taskDueDate || null,
      });
      setShowTaskModal(false);
      setTaskTitle('');
      setTaskDesc('');
      setTaskPriority('MEDIUM');
      setTaskDueDate('');
      loadTasksAndSummary(selectedProjectId);
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to create task');
    }
  };

  const updateTaskStatus = async (taskId: number, newStatus: 'TODO' | 'IN_PROGRESS' | 'DONE') => {
    try {
      await api.patch(`/tasks/${taskId}/status`, { status: newStatus });
      if (selectedProjectId) loadTasksAndSummary(selectedProjectId);
    } catch (err) {
      console.error(err);
    }
  };

  const deleteTask = async (taskId: number) => {
    if (!window.confirm('Delete this task?')) return;
    try {
      await api.delete(`/tasks/${taskId}`);
      if (selectedProjectId) loadTasksAndSummary(selectedProjectId);
    } catch (err) {
      console.error(err);
    }
  };

  // Auth Screen
  if (!token) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh', padding: '1rem' }}>
        <div style={{ background: '#1e293b', padding: '2rem', borderRadius: '12px', width: '100%', maxWidth: '400px', border: '1px solid #334155' }}>
          <h2 style={{ textAlign: 'center', marginBottom: '1.5rem', color: '#60a5fa' }}>TaskFlow</h2>
          <h3 style={{ marginBottom: '1rem', textAlign: 'center' }}>{isRegister ? 'Create Account' : 'Welcome Back'}</h3>
          
          {authError && <div style={{ color: '#ef4444', marginBottom: '1rem', fontSize: '0.9rem', textAlign: 'center' }}>{authError}</div>}
          
          <form onSubmit={handleAuth} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            {isRegister && (
              <input
                type="text"
                placeholder="Full Name"
                value={authName}
                onChange={(e) => setAuthName(e.target.value)}
                required
              />
            )}
            <input
              type="email"
              placeholder="Email Address"
              value={authEmail}
              onChange={(e) => setAuthEmail(e.target.value)}
              required
            />
            <input
              type="password"
              placeholder="Password (min 6 characters)"
              value={authPassword}
              onChange={(e) => setAuthPassword(e.target.value)}
              required
            />
            <button type="submit" style={{ background: '#3b82f6', color: '#fff', padding: '10px' }}>
              {isRegister ? 'Register' : 'Login'}
            </button>
          </form>

          <p style={{ marginTop: '1.2rem', textAlign: 'center', fontSize: '0.9rem', color: '#94a3b8' }}>
            {isRegister ? 'Already have an account?' : "Don't have an account?"}{' '}
            <span
              onClick={() => { setIsRegister(!isRegister); setAuthError(''); }}
              style={{ color: '#60a5fa', cursor: 'pointer', textDecoration: 'underline' }}
            >
              {isRegister ? 'Login here' : 'Register here'}
            </span>
          </p>
        </div>
      </div>
    );
  }

  const filteredTasks = tasks.filter((t) =>
    t.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
    (t.description && t.description.toLowerCase().includes(searchQuery.toLowerCase()))
  );

  return (
    <div style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
      {/* Top Navbar */}
      <header style={{ background: '#1e293b', borderBottom: '1px solid #334155', padding: '0.75rem 1.5rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
          <h2 style={{ color: '#60a5fa', fontWeight: 700 }}>TaskFlow</h2>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <Layers size={18} color="#94a3b8" />
            <select
              value={selectedProjectId || ''}
              onChange={(e) => setSelectedProjectId(Number(e.target.value))}
              style={{ minWidth: '160px' }}
            >
              {projects.map((p) => (
                <option key={p.id} value={p.id}>{p.name}</option>
              ))}
            </select>
            <button
              onClick={() => setShowProjectModal(true)}
              style={{ background: '#334155', color: '#f8fafc', padding: '8px 12px', display: 'flex', alignItems: 'center', gap: '4px' }}
            >
              <Plus size={16} /> New
            </button>
          </div>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
          <span style={{ fontSize: '0.9rem', color: '#94a3b8' }}>Hello, <b>{user?.name}</b></span>
          <button
            onClick={handleLogout}
            style={{ background: '#ef4444', color: '#fff', padding: '8px 12px', display: 'flex', alignItems: 'center', gap: '4px' }}
          >
            <LogOut size={16} /> Logout
          </button>
        </div>
      </header>

      {/* Main Body */}
      <main style={{ padding: '1.5rem', flex: 1, display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
        {/* Project Summary Banner */}
        {summary && (
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '1rem' }}>
            <div style={{ background: '#1e293b', padding: '1rem', borderRadius: '8px', borderLeft: '4px solid #3b82f6' }}>
              <div style={{ color: '#94a3b8', fontSize: '0.85rem' }}>Total Tasks</div>
              <div style={{ fontSize: '1.6rem', fontWeight: 700 }}>{summary.totalTasks}</div>
            </div>
            <div style={{ background: '#1e293b', padding: '1rem', borderRadius: '8px', borderLeft: '4px solid #f59e0b' }}>
              <div style={{ color: '#94a3b8', fontSize: '0.85rem' }}>To-Do</div>
              <div style={{ fontSize: '1.6rem', fontWeight: 700 }}>{summary.todoCount}</div>
            </div>
            <div style={{ background: '#1e293b', padding: '1rem', borderRadius: '8px', borderLeft: '4px solid #8b5cf6' }}>
              <div style={{ color: '#94a3b8', fontSize: '0.85rem' }}>In Progress</div>
              <div style={{ fontSize: '1.6rem', fontWeight: 700 }}>{summary.inProgressCount}</div>
            </div>
            <div style={{ background: '#1e293b', padding: '1rem', borderRadius: '8px', borderLeft: '4px solid #10b981' }}>
              <div style={{ color: '#94a3b8', fontSize: '0.85rem' }}>Completed</div>
              <div style={{ fontSize: '1.6rem', fontWeight: 700 }}>{summary.doneCount}</div>
            </div>
            <div style={{ background: '#1e293b', padding: '1rem', borderRadius: '8px', borderLeft: '4px solid #ef4444' }}>
              <div style={{ color: '#94a3b8', fontSize: '0.85rem' }}>Overdue</div>
              <div style={{ fontSize: '1.6rem', fontWeight: 700, color: '#f87171' }}>{summary.overdueCount}</div>
            </div>
          </div>
        )}

        {/* Action Controls & Filters */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem' }}>
          <div style={{ display: 'flex', gap: '0.75rem', alignItems: 'center' }}>
            <input
              type="text"
              placeholder="Search tasks..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              style={{ width: '220px' }}
            />
            <select value={priorityFilter} onChange={(e) => setPriorityFilter(e.target.value)}>
              <option value="ALL">All Priorities</option>
              <option value="LOW">Low</option>
              <option value="MEDIUM">Medium</option>
              <option value="HIGH">High</option>
            </select>
          </div>

          {selectedProjectId && (
            <button
              onClick={() => setShowTaskModal(true)}
              style={{ background: '#2563eb', color: '#fff', padding: '10px 16px', display: 'flex', alignItems: 'center', gap: '6px' }}
            >
              <Plus size={18} /> Add Task
            </button>
          )}
        </div>

        {/* Kanban Board Columns */}
        {selectedProjectId ? (
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '1.5rem', alignItems: 'start' }}>
            {(['TODO', 'IN_PROGRESS', 'DONE'] as const).map((status) => {
              const statusTasks = filteredTasks.filter((t) => t.status === status);
              const headerColors = {
                TODO: '#f59e0b',
                IN_PROGRESS: '#8b5cf6',
                DONE: '#10b981',
              };

              return (
                <div key={status} style={{ background: '#1e293b', borderRadius: '10px', padding: '1rem', border: '1px solid #334155' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem', borderBottom: `2px solid ${headerColors[status]}`, paddingBottom: '0.5rem' }}>
                    <h3 style={{ fontSize: '1rem', fontWeight: 600 }}>{status.replace('_', ' ')}</h3>
                    <span style={{ background: '#334155', padding: '2px 8px', borderRadius: '12px', fontSize: '0.8rem' }}>
                      {statusTasks.length}
                    </span>
                  </div>

                  <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem', minHeight: '300px' }}>
                    {statusTasks.map((t) => {
                      const isOverdue = t.status !== 'DONE' && t.dueDate && new Date(t.dueDate) < new Date(new Date().setHours(0, 0, 0, 0));
                      return (
                        <div
                          key={t.id}
                          style={{
                            background: '#0f172a',
                            borderRadius: '8px',
                            padding: '1rem',
                            border: `1px solid ${isOverdue ? '#ef4444' : '#334155'}`,
                            display: 'flex',
                            flexDirection: 'column',
                            gap: '0.5rem',
                          }}
                        >
                          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start' }}>
                            <h4 style={{ fontSize: '0.95rem', fontWeight: 600, color: t.status === 'DONE' ? '#94a3b8' : '#f8fafc', textDecoration: t.status === 'DONE' ? 'line-through' : 'none' }}>
                              {t.title}
                            </h4>
                            <Trash2
                              size={16}
                              color="#ef4444"
                              style={{ cursor: 'pointer', opacity: 0.8 }}
                              onClick={() => deleteTask(t.id)}
                            />
                          </div>

                          {t.description && (
                            <p style={{ fontSize: '0.85rem', color: '#94a3b8', lineHeight: 1.4 }}>{t.description}</p>
                          )}

                          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '0.5rem', fontSize: '0.75rem' }}>
                            <span
                              style={{
                                padding: '2px 6px',
                                borderRadius: '4px',
                                background: t.priority === 'HIGH' ? '#ef444422' : t.priority === 'MEDIUM' ? '#f59e0b22' : '#10b98122',
                                color: t.priority === 'HIGH' ? '#f87171' : t.priority === 'MEDIUM' ? '#fbbf24' : '#34d399',
                                fontWeight: 600,
                              }}
                            >
                              {t.priority}
                            </span>

                            {t.dueDate && (
                              <span style={{ display: 'flex', alignItems: 'center', gap: '4px', color: isOverdue ? '#f87171' : '#94a3b8' }}>
                                <Calendar size={12} /> {t.dueDate}
                              </span>
                            )}
                          </div>

                          {/* Quick Status Shift Buttons */}
                          <div style={{ display: 'flex', gap: '0.4rem', marginTop: '0.5rem' }}>
                            {status !== 'TODO' && (
                              <button
                                onClick={() => updateTaskStatus(t.id, 'TODO')}
                                style={{ flex: 1, padding: '4px', background: '#334155', color: '#f8fafc', fontSize: '0.75rem' }}
                              >
                                Todo
                              </button>
                            )}
                            {status !== 'IN_PROGRESS' && (
                              <button
                                onClick={() => updateTaskStatus(t.id, 'IN_PROGRESS')}
                                style={{ flex: 1, padding: '4px', background: '#334155', color: '#f8fafc', fontSize: '0.75rem' }}
                              >
                                Progress
                              </button>
                            )}
                            {status !== 'DONE' && (
                              <button
                                onClick={() => updateTaskStatus(t.id, 'DONE')}
                                style={{ flex: 1, padding: '4px', background: '#10b981', color: '#fff', fontSize: '0.75rem' }}
                              >
                                Done
                              </button>
                            )}
                          </div>
                        </div>
                      );
                    })}

                    {statusTasks.length === 0 && (
                      <div style={{ textAlign: 'center', color: '#64748b', fontSize: '0.85rem', padding: '2rem 0' }}>
                        No tasks
                      </div>
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        ) : (
          <div style={{ textAlign: 'center', padding: '3rem', color: '#94a3b8' }}>
            <h3>No projects created yet.</h3>
            <p style={{ marginTop: '0.5rem' }}>Click the "+ New" button on the top navigation to create your first project.</p>
          </div>
        )}
      </main>

      {/* Project Modal */}
      {showProjectModal && (
        <div style={{ position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.7)', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '1rem' }}>
          <div style={{ background: '#1e293b', padding: '2rem', borderRadius: '10px', width: '100%', maxWidth: '400px' }}>
            <h3 style={{ marginBottom: '1rem' }}>Create New Project</h3>
            <form onSubmit={createProject} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              <input
                placeholder="Project Name"
                value={newProjectName}
                onChange={(e) => setNewProjectName(e.target.value)}
                required
              />
              <textarea
                placeholder="Description (optional)"
                rows={3}
                value={newProjectDesc}
                onChange={(e) => setNewProjectDesc(e.target.value)}
              />
              <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'flex-end' }}>
                <button type="button" onClick={() => setShowProjectModal(false)} style={{ background: '#334155', padding: '8px 14px', color: '#fff' }}>Cancel</button>
                <button type="submit" style={{ background: '#2563eb', padding: '8px 14px', color: '#fff' }}>Create</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Task Modal */}
      {showTaskModal && (
        <div style={{ position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.7)', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '1rem' }}>
          <div style={{ background: '#1e293b', padding: '2rem', borderRadius: '10px', width: '100%', maxWidth: '440px' }}>
            <h3 style={{ marginBottom: '1rem' }}>Add New Task</h3>
            <form onSubmit={createTask} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              <input
                placeholder="Task Title (e.g. Implement auth)"
                value={taskTitle}
                onChange={(e) => setTaskTitle(e.target.value)}
                required
              />
              <textarea
                placeholder="Description"
                rows={3}
                value={taskDesc}
                onChange={(e) => setTaskDesc(e.target.value)}
              />
              <div style={{ display: 'flex', gap: '1rem' }}>
                <div style={{ flex: 1 }}>
                  <label style={{ fontSize: '0.8rem', color: '#94a3b8', display: 'block', marginBottom: '4px' }}>Priority</label>
                  <select
                    value={taskPriority}
                    onChange={(e: any) => setTaskPriority(e.target.value)}
                    style={{ width: '100%' }}
                  >
                    <option value="LOW">Low</option>
                    <option value="MEDIUM">Medium</option>
                    <option value="HIGH">High</option>
                  </select>
                </div>
                <div style={{ flex: 1 }}>
                  <label style={{ fontSize: '0.8rem', color: '#94a3b8', display: 'block', marginBottom: '4px' }}>Due Date</label>
                  <input
                    type="date"
                    value={taskDueDate}
                    min={new Date().toISOString().split('T')[0]}
                    onChange={(e) => setTaskDueDate(e.target.value)}
                    style={{ width: '100%' }}
                  />
                </div>
              </div>
              <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'flex-end', marginTop: '0.5rem' }}>
                <button type="button" onClick={() => setShowTaskModal(false)} style={{ background: '#334155', padding: '8px 14px', color: '#fff' }}>Cancel</button>
                <button type="submit" style={{ background: '#2563eb', padding: '8px 14px', color: '#fff' }}>Save Task</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

const API_BASE_URL = 'http://localhost:8088/api';

const MonitoringPage = () => {
    const today = new Date();
    const [selectedDate, setSelectedDate] = useState(today.toISOString().split('T')[0]);
    
    const [chartData, setChartData] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const fetchHourlyData = async () => {
        // 1. Citim datele din LocalStorage
        const token = localStorage.getItem("token");
        // IMPORTANT: Luăm userId-ul salvat la login
        const userId = localStorage.getItem("userId"); 

        if (!token) {
            setError("You are not logged in.");
            return;
        }
        if (!userId) {
            setError("User ID missing. Please relogin.");
            return;
        }

        setLoading(true);
        setError(null);
        
        const dateParam = selectedDate.split('T')[0];

        try {
            // Configurăm Axios cu Token-ul
            const api = axios.create({
                baseURL: API_BASE_URL,
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            // 2. CORECȚIA CRITICĂ ESTE AICI:
            const response = await api.get(`/monitoring/consumption`, { 
                params: { 
                    date: dateParam,
                    userId: userId // <--- ACEASTĂ LINIE LIPSEA! Backend-ul o cerea, Frontend-ul nu o dădea.
                }
            });

            if (Array.isArray(response.data)) {
                const formattedData = response.data.map(item => ({
                    name: `${item.hour.toString().padStart(2, '0')}:00`,
                    kWh: item.consumption 
                }));
                setChartData(formattedData);
            } else {
                setChartData([]);
            }

        } catch (err) {
            console.error("Error details:", err);
            setChartData([]); 
            if (err.response) {
                if (err.response.status === 404) {
                    setError("No data found for this date.");
                } else {
                    // Aici vedeai eroarea 400
                    setError(`Server Error: ${err.response.status} - ${err.response.data.message || 'Unknown'}`);
                }
            } else {
                setError("Network Error.");
            }
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchHourlyData();
    }, [selectedDate]);

    const handleDateChange = (e) => {
        setSelectedDate(e.target.value);
    };

    return (
        <div className="client-container"> 
            <h1 className="client-header">My Energy Consumption</h1>

            <div className="device-section" style={{ maxWidth: '400px', margin: '0 auto 2rem' }}>
                <div className="assign-panel" style={{ marginTop: 0 }}>
                    <label style={{ color: '#00c3ff', fontWeight: 'bold', marginRight: '1rem' }}>
                        Select Date:
                    </label>
                    <input
                        type="date"
                        value={selectedDate}
                        onChange={handleDateChange}
                        style={{
                            padding: '0.8rem',
                            borderRadius: '0.5rem',
                            border: '1px solid #00c3ff',
                            backgroundColor: '#111',
                            color: '#00c3ff',
                            outline: 'none'
                        }}
                    />
                </div>
            </div>

            <div className="device-section" style={{ width: '90%', height: '500px' }}> 
                <h2 className="client-subheader">Hourly Usage for {selectedDate}</h2>

                {loading && <p style={{ color: '#00c3ff', fontSize: '1.2rem' }}>Loading chart data...</p>}
                
                {error && <p style={{ color: '#ff4444', fontSize: '1.2rem' }}>{error}</p>}

                {!loading && !error && chartData.length === 0 && (
                    <p style={{ color: '#888', marginTop: '2rem' }}>No energy data recorded for this day.</p>
                )}

                {!loading && chartData.length > 0 && (
                    <ResponsiveContainer width="100%" height="90%">
                        <BarChart data={chartData} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
                            <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.1)" />
                            <XAxis dataKey="name" stroke="#00c3ff" />
                            <YAxis stroke="#00c3ff" label={{ value: 'kWh', angle: -90, position: 'insideLeft', fill: '#00c3ff' }} />
                            <Tooltip 
                                contentStyle={{ backgroundColor: '#1a1a1a', border: '1px solid #00c3ff', color: '#fff' }}
                                itemStyle={{ color: '#00c3ff' }}
                                cursor={{fill: 'rgba(0, 195, 255, 0.1)'}}
                            />
                            <Legend wrapperStyle={{ color: '#fff' }} />
                            <Bar dataKey="kWh" fill="#00c3ff" name="Energy Consumption" radius={[4, 4, 0, 0]} barSize={40} />
                        </BarChart>
                    </ResponsiveContainer>
                )}
            </div>
        </div>
    );
};

export default MonitoringPage;